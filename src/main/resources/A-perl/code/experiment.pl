#!/usr/bin/perl -w
# vi:si:ts=4:sw=4
use strict;
use YAML::Syck qw(LoadFile DumpFile Load Dump);
use IO::Uncompress::Bunzip2 '$Bunzip2Error';
use URL::Encode qw(url_encode_utf8);
use Number::Bytes::Human qw(format_bytes);

# TODO: no need to identify properties that frequently occur with a class.

# Step 1. find frequent classes
# Step 2. find frequent properties for frequent classes and collect triples per class
# Step 3. find linguistic patterns per class
# Step 4. collect frequent linguistic patterns for each frequent class
# Step 5. create rules

my $CFG = {
	min_class_frequency => 10000,
	min_property_frequency => 0.001,
	min_pattern_frequency => 0.001,
	min_onegram_length => 4,
	max_entities_per_class => 10000,

	R1 => 		{ min_supA => 10,	min_supB => 10,		min_supAB => 5, 	},
        R1x => 		{ min_supA => 10,	min_supB => 10,		min_supAB => 5,		},
	R1xx => 	{ min_supA => 10,	min_supB => 10,		min_supAB => 5,         },
        R1r => 		{ min_supA => 10,	min_supB => 10, 	min_supAB => 5, 	},
        R1xr => 	{ min_supA => 10,	min_supB => 10,		min_supAB => 5,         },
        R1xxr => 	{ min_supA => 10,	min_supB => 10,		min_supAB => 5,         },
        R2 => 		{ min_supA => 10,	min_supB => 10,		min_supAB => 5,         },
        R2x => 		{ min_supA => 10,	min_supB => 10,		min_supAB => 5,         },
        R2xx => 	{ min_supA => 10,	min_supB => 10,		min_supAB => 5,		},
        R2r => 		{ min_supA => 10,	min_supB => 10,		min_supAB => 5,		},
        R2xr => 	{ min_supA => 10,	min_supB => 10,		min_supAB => 5, 	},
        R2xxr => 	{ min_supA => 10,	min_supB => 10,		min_supAB => 5,		},
	
	stop => {},
	rulepattern => {
		R1 	=> 1,
		R1r 	=> 1,
		R1x 	=> 1,
		R1xr 	=> 1,
		R1xx 	=> 1,
		R1xxr 	=> 1,
		R2 	=> 1,
                R2r 	=> 1,
                R2x 	=> 1,
                R2xr 	=> 1,
                R2xx 	=> 1,
                R2xxr 	=> 1,
	},
};

open(DAT,"<../data/stopwords-en.txt");
while(defined(my $line=<DAT>)){
	next if $line =~ m/\A#/;
	$line =~ s/\n//;
	$CFG->{stop}->{$line} = 1;
}
close DAT;


my $folder_length = 4; # length of the name of the subfolder in ../data/data_per_entity/

open(LOG,">logfile.txt");


# Step 1. find frequent classes
my $frequent_class_to_entities = {};
my $entity_to_frequent_classes = {};
my $frequent_class_to_entities_file = "../data/frequent_class_to_entities-" . $CFG->{min_class_frequency} . "-" . $CFG->{max_entities_per_class} . ".yml";
my $entity_to_frequent_classes_file = "../data/entity_to_frequent_classes-" . $CFG->{min_class_frequency} . "-" . $CFG->{max_entities_per_class} . ".yml";
if(
	not -s $frequent_class_to_entities_file or
	not -s $entity_to_frequent_classes_file
){

	my $entities_with_abstract = {};
	my $entities_with_abstract_file = "../data/entities_with_abstract.yml";
	if(not -s $entities_with_abstract_file){
		print " < ../data/short-abstracts_lang=en.ttl.bz2 " . format_bytes(-s "../data/short-abstracts_lang=en.ttl.bz2") . "\n";
		my $zh = IO::Uncompress::Bunzip2->new(
			"../data/short-abstracts_lang=en.ttl.bz2",
			{ AutoClose => 1, Transparent => 1, }
		) or die "IO::Uncompress::Bunzip2 failed: $Bunzip2Error\n";

		mkdir "../data/data_per_entity/" if not -d "../data/data_per_entity/";

		while(my $line=<$zh>){
			my $obj = parse_NT_into_obj($line);
			next if not defined $obj;
			$entities_with_abstract->{$obj->{s}->{value}} = 1;
		}
		DumpFile($entities_with_abstract_file, $entities_with_abstract);
	} else {
		print " < $entities_with_abstract_file " . format_bytes(-s $entities_with_abstract_file) .  "\n";
		$entities_with_abstract = LoadFile($entities_with_abstract_file);
	}

	print " < ../data/instance-types_lang=en_specific.ttl.bz2 " . (format_bytes(-s "../data/instance-types_lang=en_specific.ttl.bz2")) ."\n";
	my $zh = IO::Uncompress::Bunzip2->new(
		"../data/instance-types_lang=en_specific.ttl.bz2",
		{ AutoClose => 1, Transparent => 1, }
	) or die "IO::Uncompress::Bunzip2 failed: $Bunzip2Error\n";

	my $cnt = 0;
	while(my $line=<$zh>){
		$cnt++;
		print "step 1 - $cnt\n" if $cnt % 100000 == 0;
		#last if $cnt > 1000000; # TODO remove
		my $obj = parse_NT_into_obj($line);
		if($obj->{o}->{value} =~ m/\A<http:\/\/dbpedia.org\/ontology\/(.*)>\Z/){
			$frequent_class_to_entities->{$1}->{$obj->{s}->{value}} = 1
			if exists $entities_with_abstract->{$obj->{s}->{value}};
		}
	}
	foreach my $c (keys %{$frequent_class_to_entities}){
		if(scalar keys %{$frequent_class_to_entities->{$c}} < $CFG->{min_class_frequency}){
			delete $frequent_class_to_entities->{$c};
		} else {
			# assumption: max_entities_per_class is much smaller than actual number of entities per class
			my $sample = {};
			my @entities = keys %{$frequent_class_to_entities->{$c}};
			while(scalar keys %{$sample} < $CFG->{max_entities_per_class}){
				my $e = $entities[rand @entities];
				$sample->{$e} = 1;
			}
			$frequent_class_to_entities->{$c} = $sample;
		}
	}
	foreach my $c (keys %{$frequent_class_to_entities}){
		foreach my $e (keys %{$frequent_class_to_entities->{$c}}){
			$entity_to_frequent_classes->{$e}->{$c} = 1;
		}
	}
	print " > $frequent_class_to_entities_file\n";
	DumpFile($frequent_class_to_entities_file, $frequent_class_to_entities);
	print " > $entity_to_frequent_classes_file\n";
	DumpFile($entity_to_frequent_classes_file, $entity_to_frequent_classes);
} else {
	print " < $frequent_class_to_entities_file " . format_bytes(-s $frequent_class_to_entities_file) .  "\n";
	$frequent_class_to_entities = LoadFile($frequent_class_to_entities_file);
	print " < $entity_to_frequent_classes_file " . format_bytes(-s $entity_to_frequent_classes_file) . "\n";
	$entity_to_frequent_classes = LoadFile($entity_to_frequent_classes_file);
}
print "done with step 1. wait.\n"; #<STDIN>;


# Step 2. find frequent properties for frequent classes and collect triples per class
my $frequent_class_to_frequent_properties = {};
my $frequent_class_to_frequent_properties_file = "../data/frequent_class_to_frequent_properties-" . 
	join("-", $CFG->{min_class_frequency}, $CFG->{min_property_frequency}, $CFG->{max_entities_per_class}) . ".yml";
my $class_to_pos_to_triples = {};
if(not -s $frequent_class_to_frequent_properties_file){
	foreach my $file (
		"../data/infobox-properties_lang=en.ttl.bz2",
		"../data/mappingbased-objects_lang=en.ttl.bz2",
		"../data/mappingbased-literals_lang=en.ttl.bz2",
	){
		print " < $file " . format_bytes(-s $file) . "\n";
		my $zh = IO::Uncompress::Bunzip2->new(
               	$file,
                	{ AutoClose => 1, Transparent => 1, }
        	) or die "IO::Uncompress::Bunzip2 failed: $Bunzip2Error\n";

		my $cnt = 0;
        	while(my $line=<$zh>){
			$cnt++;
			print "step 2 - $file - $cnt\n" if $cnt % 100000 == 0;
			#last if $cnt++ > 1000000; # TODO remove

			my $obj = parse_NT_into_obj($line);
                        next if not defined $obj;

			if($obj->{p}->{value} =~ m/\A<http:\/\/dbpedia.org\/(property|ontology)\/([^>]+)>\Z/){
				# if the entity in s-pos belongs to a frequent class, then add it. same for o-pos.
				my $p = "$1/$2";
				if(exists $entity_to_frequent_classes->{$obj->{s}->{value}}){
					foreach my $c (keys %{$entity_to_frequent_classes->{$obj->{s}->{value}}}){
						if(exists $frequent_class_to_entities->{$c}){
							$frequent_class_to_frequent_properties->{$c}->{sub}->{$p}++;
							push(@{$class_to_pos_to_triples->{$c}->{sub}}, $line);
						}
					}
				}
				if($obj->{o}->{type} eq "uri" and exists $entity_to_frequent_classes->{$obj->{o}->{value}}){
                                        foreach my $c (keys %{$entity_to_frequent_classes->{$obj->{o}->{value}}}){
						if(exists $frequent_class_to_entities->{$c}){
                                        		$frequent_class_to_frequent_properties->{$c}->{obj}->{$p}++;
							push(@{$class_to_pos_to_triples->{$c}->{obj}}, $line);
						}
                                	}
				}
			}
		}
	}

	
	foreach my $c (keys %{$frequent_class_to_frequent_properties}){
		foreach my $pos (qw(sub obj)){
			next if not exists $frequent_class_to_frequent_properties->{$c}->{$pos};
			foreach my $p (keys %{$frequent_class_to_frequent_properties->{$c}->{$pos}}){
				if($frequent_class_to_frequent_properties->{$c}->{$pos}->{$p} < $CFG->{min_property_frequency} * scalar keys %{$frequent_class_to_entities->{$c}}){
					delete $frequent_class_to_frequent_properties->{$c}->{$pos}->{$p};
					if(not scalar keys %{$frequent_class_to_frequent_properties->{$c}->{$pos}}){
						delete $frequent_class_to_frequent_properties->{$c}->{$pos};
						if(not scalar keys %{$frequent_class_to_frequent_properties->{$c}}){
                                                	delete $frequent_class_to_frequent_properties->{$c};
                                        	}
					}
				} else {
					$frequent_class_to_frequent_properties->{$c}->{$pos}->{$p} = $frequent_class_to_frequent_properties->{$c}->{$pos}->{$p} / scalar keys %{$frequent_class_to_entities->{$c}};
				}
			}
		}
	}
	#print Dump $frequent_class_to_frequent_properties;

	print " > $frequent_class_to_frequent_properties_file\n";
	DumpFile($frequent_class_to_frequent_properties_file, $frequent_class_to_frequent_properties);

	mkdir "../data/data_per_class/" if not -d "../data/data_per_class";
	foreach my $c (keys %{$class_to_pos_to_triples}){
                mkdir "../data/data_per_class/$c/" if not -d "../data/data_per_class/$c"; # TODO: is this save? better urlencode
		foreach my $pos (qw(sub obj)){
			if(not exists $class_to_pos_to_triples->{$c}->{$pos}){ # print empty files. better than missing files.
				print "empty file for class <$c>\n"; #<STDIN>; # TODO remove wait 
				print LOG "STEP 2 - empty file for class <$c> pos <$pos>.\n";
			}
			print " > ../data/data_per_class/$c/$pos-" . $CFG->{max_entities_per_class} . ".ttl\n";
			open(DAT,">../data/data_per_class/$c/$pos-" . $CFG->{max_entities_per_class} . ".ttl");
			foreach my $line (@{$class_to_pos_to_triples->{$c}->{$pos}}){
				print DAT $line;
			}
			close DAT;
			system("bzip2 ../data/data_per_class/$c/$pos-" . $CFG->{max_entities_per_class} . ".ttl");
		}
	}
} else {
	print " < $frequent_class_to_frequent_properties_file " . format_bytes(-s $frequent_class_to_frequent_properties_file) . "\n";
	$frequent_class_to_frequent_properties = LoadFile($frequent_class_to_frequent_properties_file);
}
print "done with step 2. wait.\n"; #<STDIN>;


# Step 3. create linguistic patterns per entity

# TODO: remember whether this step has been carried out already and then do not rerun it 
if(0){
	print " < ../data/short-abstracts_lang=en.ttl.bz2 " . format_bytes(-s "../data/short-abstracts_lang=en.ttl.bz2") . "\n";
        my $zh = IO::Uncompress::Bunzip2->new(
                "../data/short-abstracts_lang=en.ttl.bz2",
                { AutoClose => 1, Transparent => 1, }
        ) or die "IO::Uncompress::Bunzip2 failed: $Bunzip2Error\n";

        mkdir "../data/data_per_entity/" if not -d "../data/data_per_entity/";

        my $cnt = 0;
	while(my $line=<$zh>){
		$cnt++;
		print "step 3 - $cnt\n" if $cnt % 100000 == 0;
		#last if $cnt > 1000000; # TODO remove
		
		my $obj = parse_NT_into_obj($line);
                next if not defined $obj;

		my $e = $obj->{s}->{value};
		if(exists $entity_to_frequent_classes->{$e}){

			#next if not exists $entity_to_frequent_classes->{$e}->{Writer}; # TODO remove
			my $e_copy = $e;
			$e_copy =~ s/>\Z//;
			my $e_enc = url_encode_utf8($e);
			my $last = substr(url_encode_utf8($e_copy), -$folder_length);

			# TODO, maybe, lingistic patterns with wildcards. for these, do not distinguish between 2-grams, 3-grams etc. thus, have a patterntype "abstracted ngrams". a abstracted ngram should probably always have non-wildcards as the first and as the last token, e.g., "XXX * YYY", "XXX * YYY * ZZZ"

			my $patternfilename = "../data/data_per_entity/$last/$e_enc-" . $CFG->{min_onegram_length} . ".yml";
			if(not -s $patternfilename . ".bz2"){
				if(length "$patternfilename.bz2" <= 250){
					my $o = $obj->{o}->{value};
					$o =~ s/\A"//;
					$o =~ s/"\@en\Z//;
					my @onegrams = split(" ", $o);
					foreach my $onegram (@onegrams){
						$onegram =~ s/\.\Z//;
						$onegram =~ s/,\Z//;
						$onegram =~ s/\)\Z//;
						$onegram =~ s/\A\(//;
                                                $onegram =~ s/:\Z//;

					}
					my $onegrams_h = {};
					foreach my $onegram (@onegrams){
						$onegrams_h->{$onegram} = 1
							if
								not exists $CFG->{stop}->{lc $onegram} and
								length ($onegram) >= $CFG->{min_onegram_length};
					}
					my $twograms_h;
					for(my $i=0; $i<scalar @onegrams -1; $i++){
						my $cnt_stop = 0;
						$cnt_stop++ if exists $CFG->{stop}->{lc $onegrams[$i]};
						$cnt_stop++ if exists $CFG->{stop}->{lc $onegrams[$i+1]};
						$twograms_h->{join(" ", $onegrams[$i], $onegrams[$i+1])} = 1 if $cnt_stop < 2;	
					}
					my $threegrams_h;
					for(my $i=0; $i<scalar @onegrams -2; $i++){
						my $cnt_stop = 0;
						$cnt_stop++ if exists $CFG->{stop}->{lc $onegrams[$i]};
						$cnt_stop++ if exists $CFG->{stop}->{lc $onegrams[$i+1]};
						$cnt_stop++ if exists $CFG->{stop}->{lc $onegrams[$i+2]};
						$threegrams_h->{join(" ", $onegrams[$i], $onegrams[$i+1], $onegrams[$i+2])} = 1 if $cnt_stop < 3;
					}
					my $fourgrams_h;
					for(my $i=0; $i<scalar @onegrams -3; $i++){
						my $cnt_stop = 0;
						$cnt_stop++ if exists $CFG->{stop}->{lc $onegrams[$i]};
						$cnt_stop++ if exists $CFG->{stop}->{lc $onegrams[$i+1]};
						$cnt_stop++ if exists $CFG->{stop}->{lc $onegrams[$i+2]};
						$cnt_stop++ if exists $CFG->{stop}->{lc $onegrams[$i+3]};
						$fourgrams_h->{join(" ", $onegrams[$i], $onegrams[$i+1], $onegrams[$i+2]), $onegrams[$i+3]} = 1 if $cnt_stop < 4;
					}
					my $fivegrams_h;
                                        for(my $i=0; $i<scalar @onegrams -4; $i++){
                                                my $cnt_stop = 0;
                                                $cnt_stop++ if exists $CFG->{stop}->{lc $onegrams[$i]};
                                                $cnt_stop++ if exists $CFG->{stop}->{lc $onegrams[$i+1]};
                                                $cnt_stop++ if exists $CFG->{stop}->{lc $onegrams[$i+2]};
                                                $cnt_stop++ if exists $CFG->{stop}->{lc $onegrams[$i+3]};
                                                $cnt_stop++ if exists $CFG->{stop}->{lc $onegrams[$i+4]};
                                                $fourgrams_h->{join(" ", $onegrams[$i], $onegrams[$i+1], $onegrams[$i+2]), $onegrams[$i+3], $onegrams[$i+4]} = 1 if $cnt_stop < 5;
                                        }

					mkdir "../data/data_per_entity/$last" if not -d "../data/data_per_entity/$last";
					print " > $patternfilename\n" if rand(100) >= 99;
					DumpFileCompressed($patternfilename, {
						"1-gram" => [keys %{$onegrams_h}],
						"2-gram" => [keys %{$twograms_h}],
						"3-gram" => [keys %{$threegrams_h}],
						"4-gram" => [keys %{$fourgrams_h}],
						"5-gram" => [keys %{$fivegrams_h}],
					});
				} else {
					print "STEP 3 - cannot create file for entity <$e>\n";
	                                print LOG "STEP 3 - cannot create file for entity <$e>.\n";

				}
			}
		}
        }
}
print "done with step 3. wait.\n"; #<STDIN>;



# Step 4. collect frequent linguistic patterns for each frequent class
if(1){ # TODO run, once step 3 has processed all data
	foreach my $c (keys %{$frequent_class_to_entities}){

		#next if $c ne "Writer"; # TODO remove

		print " " x 0 . " c = $c (step 4)\n";

		# create set of linguistic patterns that sufficiently frequently occur with entities of this class

		my $L = {};
		my $patternfilename = "../data/data_per_class/$c/$c-patterns-" . $CFG->{min_onegram_length} . "-" . $CFG->{min_pattern_frequency} . "-" . $CFG->{max_entities_per_class} . ".yml";
		#unlink "$patternfilename.bz2" if -s "$patternfilename.bz2"; # TODO remove
		if(not -s "$patternfilename.bz2"){
			foreach my $e (keys %{$frequent_class_to_entities->{$c}}){
				my $e_copy = $e;
                        	$e_copy =~ s/>\Z//;
                        	my $e_enc = url_encode_utf8($e);
                        	my $last = substr(url_encode_utf8($e_copy), -$folder_length);
				if(-s "../data/data_per_entity/$last/$e_enc-" . $CFG->{min_onegram_length} . ".yml.bz2"){
					my $data = LoadFileCompressed("../data/data_per_entity/$last/$e_enc-" . $CFG->{min_onegram_length} . ".yml");
					foreach my $patterntype (keys %{$data}){
						foreach my $pattern (@{$data->{$patterntype}}){
							$L->{$patterntype}->{$pattern}->{$e} = 1;
						}
					}
				} else {
	                                print LOG "STEP 4 - pattern file missing for entity <$e>, class <$c>. filename: <../data/data_per_entity/$last/$e_enc-" . $CFG->{min_onegram_length} . ".yml>.\n";

					print "file does not exist ($c): ../data/data_per_entity/$last/$e_enc-" . $CFG->{min_onegram_length} . ".yml\n";
				}
			}
			foreach my $patterntype (keys %{$L}){
				foreach my $pattern (keys %{$L->{$patterntype}}){
					if(scalar keys %{$L->{$patterntype}->{$pattern}} < $CFG->{min_pattern_frequency} * scalar keys %{$frequent_class_to_entities->{$c}}){
						delete $L->{$patterntype}->{$pattern};
					}
				}
			}
			print " > $patternfilename\n";
			DumpFileCompressed($patternfilename, $L);
		}
	}
}
print "done with step 4. wait.\n";


print LOG "done.\n";
close LOG;


sub shorten_property {
	my $p = shift;
	if($p =~ m/<http:\/\/dbpedia.org\/ontology\/(.*)>/){
		return "ontology/$1";
	}
	elsif($p =~ m/<http:\/\/dbpedia.org\/property\/(.*)>/){
		return "property/$1";
	}
	else {
		return undef;
	}
}



sub parse_NT_into_obj {
        my $string = shift;

	return undef if $string =~ m/\A#/;

        # URI URI URI
        # URI URI LIT-LANG
        # URI URI LIT-DAT
        # URI URI BNODE

        # BNODE URI URI
        # BNODE URI LIT-LANG
        # BNODE URI LIT-DAT
        # BNODE URI BNODE

        # URI URI URI
        if($string =~ m/<(.+)>(?:\s|\t)<(.+)>(?:\s|\t)<(.+)> .\n\Z/){
                return {
                        s => { type => "uri", value => &shorten("<$1>") },
                        p => { type => "uri", value => &shorten("<$2>") },
                        o => { type => "uri", value => &shorten("<$3>") },
                };
        }

        # URI URI LIT-LANG
        elsif($string =~ m/<(.+)>(?:\s|\t)<(.+)>(?:\s|\t)\"(.*)\"\@(.+) .\n\Z/){
                return {
                        s => { type => "uri", value => &shorten("<$1>") },
                        p => { type => "uri", value => &shorten("<$2>") },
                        o => { type => "literal", value => "\"$3\"\@" . &shorten($4) },
                };
        }

        # URI URI LIT-DAT
        elsif($string =~ m/<(.+)>(?:\s|\t)<(.+)>(?:\s|\t)\"(.+)\"\^\^<(.*)> .\n\Z/){
                return {
                        s => { type => "uri", value => &shorten("<$1>") },
                        p => { type => "uri", value => &shorten("<$2>") },
                        o => { type => "typed-literal", value => "\"$3\"\^\^" . &shorten("<$4>") },
                };
        }

        # URI URI LIT
        elsif($string =~ m/<(.+)>(?:\s|\t)<(.+)>(?:\s|\t)\"(.*)\" .\n\Z/){
                return {
                        s => { type => "uri", value => &shorten("<$1>") },
                        p => { type => "uri", value => &shorten("<$2>") },
                        o => { type => "literal", value => "\"$3\"" },
                };
        }

        else {
                print "Cannot handle line format: <$string>\n"; #<STDIN>;
                print LOG "parse_NT_into_obj - cannot handle line format <$string>.\n";
		return undef; # { s => {}, p => {}, o => {}};
        }
}

sub shorten {
	return $_[0];
}

sub DumpFileCompressed {
	my ($filename, $data) = @_;
	DumpFile($filename, $data);
	system("bzip2 $filename");
}

sub LoadFileCompressed {
	my $filename = shift;
	my $data = q{};
	#print " < $filename.bz2 " . format_bytes(-s "$filename.bz2") . "\n";
	my $zh = IO::Uncompress::Bunzip2->new(
		"$filename.bz2",  
		{ AutoClose => 1, Transparent => 1, }
	) or die "IO::Uncompress::Bunzip2 failed: $Bunzip2Error\n";
	while(my $line=<$zh>){
		$data .= $line;
	}
	return Load($data);
}
