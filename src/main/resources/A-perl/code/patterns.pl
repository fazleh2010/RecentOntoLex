#!/usr/bin/perl -w
use strict;
use YAML::Syck qw(LoadFile DumpFile Load Dump);
use IO::Uncompress::Bunzip2 '$Bunzip2Error';
use URL::Encode qw(url_encode_utf8);
use Number::Bytes::Human qw(format_bytes);

# extract ngrams from all abstracts

my $CFG = {
        min_onegram_length => 4,
};

open(DAT,"<../data/stopwords-en.txt");
while(defined(my $line=<DAT>)){
	next if $line =~ m/\A#/;
	$line =~ s/\n//;
	$CFG->{stop}->{$line} = 1;
}
close DAT;

my $folder_length = 4;


print " < ../data/short-abstracts_lang=en.ttl.bz2 " . format_bytes(-s "../data/short-abstracts_lang=en.ttl.bz2") . "\n";
my $zh = IO::Uncompress::Bunzip2->new(
	"../data/short-abstracts_lang=en.ttl.bz2",
        { AutoClose => 1, Transparent => 1, }
) or die "IO::Uncompress::Bunzip2 failed: $Bunzip2Error\n";

mkdir "../data/data_per_entity/" if not -d "../data/data_per_entity/";

my $cnt = 0;
while(my $line=<$zh>){
	$cnt++;
	next if $cnt < 2000000;
	print "patterns - $cnt\n" if $cnt % 100 == 0;
	#last if $cnt > 1000; # TODO remove

	my $obj = parse_NT_into_obj($line);
	next if not defined $obj;

	my $e = $obj->{s}->{value};
	my $e_copy = $e;
	$e_copy =~ s/>\Z//;
	my $e_enc = url_encode_utf8($e);
	my $last = substr(url_encode_utf8($e_copy), -$folder_length);

	my $patternfilename = "../data/data_per_entity/$last/$e_enc-" . $CFG->{min_onegram_length} . ".yml";
	unlink "$patternfilename.bz2" if -s "$patternfilename.bz2";

	if(1 or not -s $patternfilename . ".bz2"){ # TODO remove "1 or"
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
			#print " > $patternfilename\n" if rand(100) >= 99;
			DumpFileCompressed($patternfilename, {
					"1-gram" => [keys %{$onegrams_h}],
					"2-gram" => [keys %{$twograms_h}],
					"3-gram" => [keys %{$threegrams_h}],
					"4-gram" => [keys %{$fourgrams_h}],
					"5-gram" => [keys %{$fivegrams_h}],
				});
		} else {
			print "STEP 3 - cannot create file for entity <$e>\n";

		}

	}
}

sub DumpFileCompressed {
        my ($filename, $data) = @_;
        DumpFile($filename, $data);
        system("bzip2 $filename");
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
                return undef; # { s => {}, p => {}, o => {}};
        }
}

sub shorten {
        return $_[0];
}

