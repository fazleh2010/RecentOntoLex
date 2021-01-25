#!/usr/bin/perl -w
# vi:si:ts=4:sw=4
use strict;
use YAML::Syck qw(LoadFile DumpFile Load Dump);
use IO::Uncompress::Bunzip2 '$Bunzip2Error';
use URL::Encode qw(url_encode_utf8);
use Number::Bytes::Human qw(format_bytes);

my $min = {
        min_supA => 10,
        min_supB => 10,
        min_supAB => 10,
};

my $CFG = {
        min_class_frequency => 10000,
        min_property_frequency => 0.001,
        min_pattern_frequency => 0.001,
        min_onegram_length => 4,
        max_entities_per_class => 10000,

	predict_l_for_s_given_po	=> $min,
	predict_l_for_s_given_p 	=> $min,
	predict_l_for_s_given_o		=> $min,
	predict_l_for_o_given_sp	=> $min,
        predict_l_for_o_given_s		=> $min,
        predict_l_for_o_given_p		=> $min,
	predict_po_for_s_given_l	=> $min,
        predict_p_for_s_given_l		=> $min,
        predict_o_for_s_given_l		=> $min,
	predict_sp_for_o_given_l	=> $min,
        predict_s_for_o_given_l		=> $min,
        predict_p_for_o_given_l		=> $min,

        rulepattern => {
		predict_l_for_s_given_po        => 1, # checked, works
        	predict_l_for_s_given_p         => 1, # checked, works
        	predict_l_for_s_given_o         => 1, # checked, works
        	predict_l_for_o_given_sp        => 1, # checked, works
        	predict_l_for_o_given_s         => 1, # checked, works
        	predict_l_for_o_given_p         => 1, # checked, works
        	predict_po_for_s_given_l        => 1, # checked, works
        	predict_p_for_s_given_l         => 1, # checked, works
        	predict_o_for_s_given_l         => 1, # checked, works
        	predict_sp_for_o_given_l        => 1, # checked, works
        	predict_s_for_o_given_l         => 1, # checked, works
        	predict_p_for_o_given_l         => 1, # checked, works
        },
};

my $frequent_class_to_entities_file = "../data/frequent_class_to_entities-" . $CFG->{min_class_frequency} . "-" . $CFG->{max_entities_per_class} . ".yml";
print " < $frequent_class_to_entities_file " . format_bytes(-s $frequent_class_to_entities_file) .  "\n";
my $frequent_class_to_entities = LoadFile($frequent_class_to_entities_file);

FEC: foreach my $c (sort keys %{$frequent_class_to_entities}){

	#next if $c ne "Person"; # TODO remove

	# skip class if for one rulepattern results have been created already
	
	foreach my $rulepattern (keys %{$CFG->{rulepattern}}){
		my $rulefilename  = "../data/data_per_class/$c/$c-rules-$rulepattern-"
			. join("-",
				$CFG->{min_pattern_frequency},
				$CFG->{max_entities_per_class},
				$CFG->{$rulepattern}->{min_supA},
				$CFG->{$rulepattern}->{min_supB},
				$CFG->{$rulepattern}->{min_supAB}
			) . ".yml"
		;
		next FEC if -e $rulefilename;
	}

	print "class: $c\n";

	my $patternfilename = "../data/data_per_class/$c/$c-patterns-" . $CFG->{min_onegram_length} . "-" . $CFG->{min_pattern_frequency} . "-" . $CFG->{max_entities_per_class} . ".yml";
	my $L = LoadFileCompressed($patternfilename);
	my $R = {};

	foreach my $pos (qw(sub obj)){ 

		next if $pos eq "sub"
			and not $CFG->{rulepattern}->{predict_l_for_s_given_po}
			and not $CFG->{rulepattern}->{predict_l_for_s_given_p}
			and not $CFG->{rulepattern}->{predict_l_for_s_given_o}
			and not $CFG->{rulepattern}->{predict_po_for_s_given_l}
			and not $CFG->{rulepattern}->{predict_p_for_s_given_l}
			and not $CFG->{rulepattern}->{predict_o_for_s_given_l};

		next if $pos eq "obj"
			and not $CFG->{rulepattern}->{predict_l_for_o_given_sp}
			and not $CFG->{rulepattern}->{predict_l_for_o_given_s}
			and not $CFG->{rulepattern}->{predict_l_for_o_given_p}
			and not $CFG->{rulepattern}->{predict_sp_for_o_given_l}
			and not $CFG->{rulepattern}->{predict_s_for_o_given_l}
			and not $CFG->{rulepattern}->{predict_p_for_o_given_l};

		my $datafilename = "../data/data_per_class/$c/$pos-" . $CFG->{max_entities_per_class} . ".ttl";

		if(-s "$datafilename.bz2"){

			my $D = {};
			print " < $datafilename.bz2 " . format_bytes(-s "$datafilename.bz2") . "\n";
			my $zh = IO::Uncompress::Bunzip2->new(
				"$datafilename.bz2",
				{ AutoClose => 1, Transparent => 1, }
			) or die "IO::Uncompress::Bunzip2 failed: $Bunzip2Error\n";
			while(my $line=<$zh>){
				my $obj = parse_NT_into_obj($line);
				next if not defined $obj;
				my $p = &shorten_property($obj->{p}->{value});
				next if not defined $p;
				my $s = $obj->{s}->{value};
				my $o = $obj->{o}->{value};

				if($pos eq "sub"){
					foreach my $patterntype (keys %{$L}){
						foreach my $l (keys %{$L->{$patterntype}}){
							if(exists $L->{$patterntype}->{$l}->{$s}){
								$D->{predict_l_for_s_given_po}->{$p}->{$o}->{A}->{$patterntype}->{$l}->{$s} = 1
								if $CFG->{rulepattern}->{predict_l_for_s_given_po};
								$D->{predict_l_for_s_given_p}->{$p}->{A}->{$patterntype}->{$l}->{$s} = 1
								if $CFG->{rulepattern}->{predict_l_for_s_given_p};
								$D->{predict_l_for_s_given_o}->{$o}->{A}->{$patterntype}->{$l}->{$s} = 1
								if $CFG->{rulepattern}->{predict_l_for_s_given_o};
							}
						}
					}

					$D->{predict_l_for_s_given_po}->{$p}->{$o}->{B}->{$s} = 1
					if $CFG->{rulepattern}->{predict_l_for_s_given_po};
					$D->{predict_l_for_s_given_p}->{$p}->{B}->{$s} = 1
					if $CFG->{rulepattern}->{predict_l_for_s_given_p};
					$D->{predict_l_for_s_given_o}->{$o}->{B}->{$s} = 1
					if $CFG->{rulepattern}->{predict_l_for_s_given_o};
				}

				if($pos eq "obj"){
					foreach my $patterntype (keys %{$L}){
						foreach my $l (keys %{$L->{$patterntype}}){
							if(exists $L->{$patterntype}->{$l}->{$o}){
								$D->{predict_l_for_o_given_sp}->{$s}->{$p}->{A}->{$patterntype}->{$l}->{$o} = 1
								if $CFG->{rulepattern}->{predict_l_for_o_given_sp};
								$D->{predict_l_for_o_given_s}->{$s}->{A}->{$patterntype}->{$l}->{$o} = 1
								if $CFG->{rulepattern}->{predict_l_for_o_given_s};
								$D->{predict_l_for_o_given_p}->{$p}->{A}->{$patterntype}->{$l}->{$o} = 1
								if $CFG->{rulepattern}->{predict_l_for_o_given_p};
							}
						}
					}

					$D->{predict_l_for_o_given_sp}->{$s}->{$p}->{B}->{$o} = 1
					if $CFG->{rulepattern}->{predict_l_for_o_given_sp};
					$D->{predict_l_for_o_given_s}->{$s}->{B}->{$o} = 1
					if $CFG->{rulepattern}->{predict_l_for_o_given_s};
					$D->{predict_l_for_o_given_p}->{$p}->{B}->{$o} = 1
					if $CFG->{rulepattern}->{predict_l_for_o_given_p};
				}
			}


			if($CFG->{rulepattern}->{predict_l_for_s_given_po} or $CFG->{rulepattern}->{predict_po_for_s_given_l}){
				foreach my $p (keys %{$D->{predict_l_for_s_given_po}}){
					foreach my $o (keys %{$D->{predict_l_for_s_given_po}->{$p}}){
						my $supB = scalar keys %{$D->{predict_l_for_s_given_po}->{$p}->{$o}->{B}};
						foreach my $patterntype (keys %{$D->{predict_l_for_s_given_po}->{$p}->{$o}->{A}}){
							foreach my $l (keys %{$D->{predict_l_for_s_given_po}->{$p}->{$o}->{A}->{$patterntype}}){
								my $supA = scalar keys %{$D->{predict_l_for_s_given_po}->{$p}->{$o}->{A}->{$patterntype}->{$l}};
								my $supAB = 0;
								foreach my $e (keys %{$D->{predict_l_for_s_given_po}->{$p}->{$o}->{A}->{$patterntype}->{$l}}){
									$supAB++ if exists $D->{predict_l_for_s_given_po}->{$p}->{$o}->{B}->{$e};
								}
								if(
									$CFG->{rulepattern}->{predict_l_for_s_given_po} and
									$supA >= $CFG->{predict_l_for_s_given_po}->{min_supA} and
									$supB >= $CFG->{predict_l_for_s_given_po}->{min_supB} and
									$supAB >= $CFG->{predict_l_for_s_given_po}->{min_supAB}
								){
									my $condBA = $supAB/$supA;
									my $condAB = $supAB/$supB;
									push(@{$R->{predict_l_for_s_given_po}}, &new_rule({
										c => $c, p => $p, o => $o, l => $l,
										ruletype => "predict_l_for_s_given_po",
										patterntype => $patterntype,
										supA => $supA, supB => $supB, supAB => $supAB,
										condAB => $condAB, condBA => $condBA,
										#entities_for_A => [keys %{$D->{predict_l_for_s_given_po}->{$p}->{$o}->{A}->{$patterntype}->{$l}}],
										#entities_for_B => [keys %{$D->{predict_l_for_s_given_po}->{$p}->{$o}->{B}}]
									}));
									#print "found predict_l_for_s_given_po\n";
								} # predict_l_for_s_given_po

								my $supA_r = $supB;
								my $supB_r = $supA;
								if(
									$CFG->{rulepattern}->{predict_po_for_s_given_l} and
									$supA_r >= $CFG->{predict_po_for_s_given_l}->{min_supA} and
									$supB_r >= $CFG->{predict_po_for_s_given_l}->{min_supB} and
									$supAB >= $CFG->{predict_po_for_s_given_l}->{min_supAB}
								){
									my $condBA = $supAB/$supA_r;
									my $condAB = $supAB/$supB_r;
									push(@{$R->{predict_po_for_s_given_l}}, &new_rule({
										c => $c, p => $p, o => $o, l => $l,
										ruletype => "predict_po_for_s_given_l",
										patterntype => $patterntype,
										supA => $supA_r, supB => $supB_r, supAB => $supAB,
										condAB => $condAB, condBA => $condBA,
										#entities_for_A => [keys %{$D->{predict_l_for_s_given_po}->{$p}->{$o}->{A}->{$patterntype}->{$l}}],
										#entities_for_B => [keys %{$D->{predict_l_for_s_given_po}->{$p}->{$o}->{B}}]
									}));
									#print "found predict_po_for_s_given_l\n";
								} # predict_po_for_s_given_l
							} # foreach l
						} # foreach patterntype
					} # foreach o
				} # foreach p
			} # predict_l_for_s_given_po, predict_po_for_s_given_l



			if($CFG->{rulepattern}->{predict_l_for_s_given_p} or $CFG->{rulepattern}->{predict_p_for_s_given_l}){
				foreach my $p (keys %{$D->{predict_l_for_s_given_p}}){
					my $supB = scalar keys %{$D->{predict_l_for_s_given_p}->{$p}->{B}};
					foreach my $patterntype (keys %{$D->{predict_l_for_s_given_p}->{$p}->{A}}){
						foreach my $l (keys %{$D->{predict_l_for_s_given_p}->{$p}->{A}->{$patterntype}}){
							my $supA = scalar keys %{$D->{predict_l_for_s_given_p}->{$p}->{A}->{$patterntype}->{$l}};
							my $supAB = 0;
							foreach my $e (keys %{$D->{predict_l_for_s_given_p}->{$p}->{A}->{$patterntype}->{$l}}){
								$supAB++ if exists $D->{predict_l_for_s_given_p}->{$p}->{B}->{$e};
							}
							if(
								$CFG->{rulepattern}->{predict_l_for_s_given_p} and
								$supA >= $CFG->{predict_l_for_s_given_p}->{min_supA} and
								$supB >= $CFG->{predict_l_for_s_given_p}->{min_supB} and
								$supAB >= $CFG->{predict_l_for_s_given_p}->{min_supAB}
							){
								my $condBA = $supAB/$supA;
								my $condAB = $supAB/$supB;
								push(@{$R->{predict_l_for_s_given_p}}, &new_rule({
									c => $c, p => $p, l => $l,
									ruletype => "predict_l_for_s_given_p",
									patterntype => $patterntype,
									supA => $supA, supB => $supB, supAB => $supAB,
									condAB => $condAB, condBA => $condBA,
									#entities_for_A => [keys %{$D->{predict_l_for_s_given_p}->{$p}->{$o}->{A}->{$patterntype}->{$l}}],
									#entities_for_B => [keys %{$D->{predict_l_for_s_given_p}->{$p}->{$o}->{B}}]
								}));
								#print "found predict_l_for_s_given_p\n";
							} # predict_l_for_s_given_p

							my $supA_r = $supB;
							my $supB_r = $supA;
							if(
								$CFG->{rulepattern}->{predict_p_for_s_given_l} and
								$supA_r >= $CFG->{predict_p_for_s_given_l}->{min_supA} and
								$supB_r >= $CFG->{predict_p_for_s_given_l}->{min_supB} and
								$supAB >= $CFG->{predict_p_for_s_given_l}->{min_supAB}
							){
								my $condBA = $supAB/$supA_r;
								my $condAB = $supAB/$supB_r;
								push(@{$R->{predict_p_for_s_given_l}}, &new_rule({
									c => $c, p => $p, l => $l,
									ruletype => "predict_p_for_s_given_l",
									patterntype => $patterntype,
									supA => $supA_r, supB => $supB_r, supAB => $supAB,
									condAB => $condAB, condBA => $condBA,
									#entities_for_A => [keys %{$D->{predict_l_for_s_given_p}->{$p}->{$o}->{A}->{$patterntype}->{$l}}],
									#entities_for_B => [keys %{$D->{predict_l_for_s_given_p}->{$p}->{$o}->{B}}]
								}));
								#print "found predict_p_for_s_given_l\n";
							} # predict_p_for_s_given_l
						} # foreach l
					} # foreach patterntype
				} # foreach p
			} # predict_l_for_s_given_p, predict_p_for_s_given_l



			if($CFG->{rulepattern}->{predict_l_for_s_given_o} or $CFG->{rulepattern}->{predict_o_for_s_given_l}){
				foreach my $o (keys %{$D->{predict_l_for_s_given_o}}){
					my $supB = scalar keys %{$D->{predict_l_for_s_given_o}->{$o}->{B}};
					foreach my $patterntype (keys %{$D->{predict_l_for_s_given_o}->{$o}->{A}}){
						foreach my $l (keys %{$D->{predict_l_for_s_given_o}->{$o}->{A}->{$patterntype}}){
							my $supA = scalar keys %{$D->{predict_l_for_s_given_o}->{$o}->{A}->{$patterntype}->{$l}};
							my $supAB = 0;
							foreach my $e (keys %{$D->{predict_l_for_s_given_o}->{$o}->{A}->{$patterntype}->{$l}}){
								$supAB++ if exists $D->{predict_l_for_s_given_o}->{$o}->{B}->{$e};
							}
							if(
								$CFG->{rulepattern}->{predict_l_for_s_given_o} and
								$supA >= $CFG->{predict_l_for_s_given_o}->{min_supA} and
								$supB >= $CFG->{predict_l_for_s_given_o}->{min_supB} and
								$supAB >= $CFG->{predict_l_for_s_given_o}->{min_supAB}
							){
								my $condBA = $supAB/$supA;
								my $condAB = $supAB/$supB;
								push(@{$R->{predict_l_for_s_given_o}}, &new_rule({
									c => $c, o => $o, l => $l,
									ruletype => "predict_l_for_s_given_o",
									patterntype => $patterntype,
									supA => $supA, supB => $supB, supAB => $supAB,
									condAB => $condAB, condBA => $condBA,
									#entities_for_A => [keys %{$D->{predict_l_for_s_given_o}->{$o}->{A}->{$patterntype}->{$l}}],
									#entities_for_B => [keys %{$D->{predict_l_for_s_given_o}->{$o}->{B}}]
								}));
								#print "found predict_l_for_s_given_o\n";
							} # predict_l_for_s_given_o

							my $supA_r = $supB;
							my $supB_r = $supA;
							if(
								$CFG->{rulepattern}->{predict_o_for_s_given_l} and
								$supA_r >= $CFG->{predict_o_for_s_given_l}->{min_supA} and
								$supB_r >= $CFG->{predict_o_for_s_given_l}->{min_supB} and
								$supAB >= $CFG->{predict_o_for_s_given_l}->{min_supAB}
							){
								my $condBA = $supAB/$supA_r;
								my $condAB = $supAB/$supB_r;
								push(@{$R->{predict_o_for_s_given_l}}, &new_rule({
									c => $c, o => $o, l => $l,
									ruletype => "predict_o_for_s_given_l",
									patterntype => $patterntype,
									supA => $supA_r, supB => $supB_r, supAB => $supAB,
									condAB => $condAB, condBA => $condBA,
									#entities_for_A => [keys %{$D->{predict_l_for_s_given_o}->{$o}->{A}->{$patterntype}->{$l}}],
									#entities_for_B => [keys %{$D->{predict_l_for_s_given_o}->{$o}->{B}}]
								}));
								#print "found predict_o_for_s_given_l\n";
							} # predict_o_for_s_given_l
						} # foreach l
					} # foreach patterntype
				} # foreach o
			} # predict_l_for_s_given_o, predict_o_for_s_given_l


			if(
				$CFG->{rulepattern}->{predict_l_for_o_given_sp} or
				$CFG->{rulepattern}->{predict_sp_for_o_given_l}
			){
				foreach my $s (keys %{$D->{predict_l_for_o_given_sp}}){
					foreach my $p (keys %{$D->{predict_l_for_o_given_sp}->{$s}}){
						my $supB = scalar keys %{$D->{predict_l_for_o_given_sp}->{$s}->{$p}->{B}};
						foreach my $patterntype (keys %{$D->{predict_l_for_o_given_sp}->{$s}->{$p}->{A}}){
							foreach my $l (keys %{$D->{predict_l_for_o_given_sp}->{$s}->{$p}->{A}->{$patterntype}}){
								my $supA = scalar keys %{$D->{predict_l_for_o_given_sp}->{$s}->{$p}->{A}->{$patterntype}->{$l}};
								my $supAB = 0;
								foreach my $e (keys %{$D->{predict_l_for_o_given_sp}->{$s}->{$p}->{A}->{$patterntype}->{$l}}){
									$supAB++ if exists $D->{predict_l_for_o_given_sp}->{$s}->{$p}->{B}->{$e};
								}
								if(
									$CFG->{rulepattern}->{predict_l_for_o_given_sp} and
									$supA >= $CFG->{predict_l_for_o_given_sp}->{min_supA} and
									$supB >= $CFG->{predict_l_for_o_given_sp}->{min_supB} and
									$supAB >= $CFG->{predict_l_for_o_given_sp}->{min_supAB}
								){
									my $condBA = $supAB/$supA;
									my $condAB = $supAB/$supB;
									push(@{$R->{predict_l_for_o_given_sp}}, &new_rule({
										c => $c, s => $s, p => $p, l => $l,
										ruletype => "predict_l_for_o_given_sp",
										patterntype => $patterntype,
										supA => $supA, supB => $supB, supAB => $supAB,
										condAB => $condAB, condBA => $condBA,
										#entities_for_A => [keys %{$D->{predict_l_for_o_given_sp}->{$s}->{$p}->{A}->{$patterntype}->{$l}}],
										#entities_for_B => [keys %{$D->{predict_l_for_o_given_sp}->{$s}->{$p}->{B}}]
									}));
									#print "found predict_l_for_o_given_sp\n";
								} # predict_l_for_o_given_sp

								my $supA_r = $supB;
								my $supB_r = $supA;
								if(
									$CFG->{rulepattern}->{predict_sp_for_o_given_l} and
									$supA_r >= $CFG->{predict_sp_for_o_given_l}->{min_supA} and
									$supB_r >= $CFG->{predict_sp_for_o_given_l}->{min_supB} and
									$supAB >= $CFG->{predict_sp_for_o_given_l}->{min_supAB}
								){
									my $condBA = $supAB/$supA_r;
									my $condAB = $supAB/$supB_r;
									push(@{$R->{predict_sp_for_o_given_l}}, &new_rule({
										c => $c, s => $s, p => $p, l => $l,
										ruletype => "predict_sp_for_o_given_l",
										patterntype => $patterntype,
										supA => $supA_r, supB => $supB_r, supAB => $supAB,
										condAB => $condAB, condBA => $condBA,
										#entities_for_A => [keys %{$D->{predict_l_for_o_given_sp}->{$s}->{$p}->{A}->{$patterntype}->{$l}}],
										#entities_for_B => [keys %{$D->{predict_l_for_o_given_sp}->{$s}->{$p}->{B}}]
									}));
									#print "found predict_sp_for_o_given_l\n";
								} # predict_sp_for_o_given_l
							} # foreach l
						} # foreach patterntype
					} # foreach p
				} # foreach s
			} # predict_l_for_o_given_sp, predict_sp_for_o_given_l

			if(
				$CFG->{rulepattern}->{predict_l_for_o_given_s} or
				$CFG->{rulepattern}->{predict_s_for_o_given_l}
			){
				foreach my $s (keys %{$D->{predict_l_for_o_given_s}}){
					my $supB = scalar keys %{$D->{predict_l_for_o_given_s}->{$s}->{B}};
					foreach my $patterntype (keys %{$D->{predict_l_for_o_given_s}->{$s}->{A}}){
						foreach my $l (keys %{$D->{predict_l_for_o_given_s}->{$s}->{A}->{$patterntype}}){
							my $supA = scalar keys %{$D->{predict_l_for_o_given_s}->{$s}->{A}->{$patterntype}->{$l}};
							my $supAB = 0;
							foreach my $e (keys %{$D->{predict_l_for_o_given_s}->{$s}->{A}->{$patterntype}->{$l}}){
								$supAB++ if exists $D->{predict_l_for_o_given_s}->{$s}->{B}->{$e};
							}
							if(
								$CFG->{rulepattern}->{predict_l_for_o_given_s} and
								$supA >= $CFG->{predict_l_for_o_given_s}->{min_supA} and
								$supB >= $CFG->{predict_l_for_o_given_s}->{min_supB} and
								$supAB >= $CFG->{predict_l_for_o_given_s}->{min_supAB}
							){
								my $condBA = $supAB/$supA;
								my $condAB = $supAB/$supB;
								push(@{$R->{predict_l_for_o_given_s}}, &new_rule({
									c => $c, s => $s, l => $l,
									ruletype => "predict_l_for_o_given_s",
									patterntype => $patterntype,
									supA => $supA, supB => $supB, supAB => $supAB,
									condAB => $condAB, condBA => $condBA,
									#entities_for_A => [keys %{$D->{predict_l_for_o_given_s}->{$s}->{A}->{$patterntype}->{$l}}],
									#entities_for_B => [keys %{$D->{predict_l_for_o_given_s}->{$s}->{B}}]
								}));
								#print "found predict_l_for_o_given_s\n";
							} # predict_l_for_o_given_s

							my $supA_r = $supB;
							my $supB_r = $supA;
							if(
								$CFG->{rulepattern}->{predict_s_for_o_given_l} and
								$supA_r >= $CFG->{predict_s_for_o_given_l}->{min_supA} and
								$supB_r >= $CFG->{predict_s_for_o_given_l}->{min_supB} and
								$supAB >= $CFG->{predict_s_for_o_given_l}->{min_supAB}
							){
								my $condBA = $supAB/$supA_r;
								my $condAB = $supAB/$supB_r;
								push(@{$R->{predict_s_for_o_given_l}}, &new_rule({
									c => $c, s => $s, l => $l,
									ruletype => "predict_s_for_o_given_l",
									patterntype => $patterntype,
									supA => $supA_r, supB => $supB_r, supAB => $supAB,
									condAB => $condAB, condBA => $condBA,
									#entities_for_A => [keys %{$D->{predict_l_for_o_given_s}->{$s}->{$p}->{A}->{$patterntype}->{$l}}],
									#entities_for_B => [keys %{$D->{predict_l_for_o_given_s}->{$s}->{$p}->{B}}]
								}));
								#print "found predict_s_for_o_given_l\n";
							} # predict_s_for_o_given_l
						} # foreach l
					} # foreach patterntype
				} # foreach s
			} # predict_l_for_o_given_s, predict_s_for_o_given_l



			if(
				$CFG->{rulepattern}->{predict_l_for_o_given_p} or
				$CFG->{rulepattern}->{predict_p_for_o_given_l}
			){
				foreach my $p (keys %{$D->{predict_l_for_o_given_p}}){
					my $supB = scalar keys %{$D->{predict_l_for_o_given_p}->{$p}->{B}};
					foreach my $patterntype (keys %{$D->{predict_l_for_o_given_p}->{$p}->{A}}){
						foreach my $l (keys %{$D->{predict_l_for_o_given_p}->{$p}->{A}->{$patterntype}}){
							my $supA = scalar keys %{$D->{predict_l_for_o_given_p}->{$p}->{A}->{$patterntype}->{$l}};
							my $supAB = 0;
							foreach my $e (keys %{$D->{predict_l_for_o_given_p}->{$p}->{A}->{$patterntype}->{$l}}){
								$supAB++ if exists $D->{predict_l_for_o_given_p}->{$p}->{B}->{$e};
							}
							if(
								$CFG->{rulepattern}->{predict_l_for_o_given_p} and
								$supA >= $CFG->{predict_l_for_o_given_p}->{min_supA} and
								$supB >= $CFG->{predict_l_for_o_given_p}->{min_supB} and
								$supAB >= $CFG->{predict_l_for_o_given_p}->{min_supAB}
							){
								my $condBA = $supAB/$supA;
								my $condAB = $supAB/$supB;
								push(@{$R->{predict_l_for_o_given_p}}, &new_rule({
									c => $c, p => $p, l => $l,
									ruletype => "predict_l_for_o_given_p",
									patterntype => $patterntype,
									supA => $supA, supB => $supB, supAB => $supAB,
									condAB => $condAB, condBA => $condBA,
									#entities_for_A => [keys %{$D->{predict_l_for_o_given_p}->{$p}->{A}->{$patterntype}->{$l}}],
									#entities_for_B => [keys %{$D->{predict_l_for_o_given_p}->{$p}->{B}}]
								}));
								#print "found predict_l_for_o_given_p\n";
							} # predict_l_for_o_given_p

							my $supA_r = $supB;
							my $supB_r = $supA;
							if(
								$CFG->{rulepattern}->{predict_p_for_o_given_l} and
								$supA_r >= $CFG->{predict_p_for_o_given_l}->{min_supA} and
								$supB_r >= $CFG->{predict_p_for_o_given_l}->{min_supB} and
								$supAB >= $CFG->{predict_p_for_o_given_l}->{min_supAB}
							){
								my $condBA = $supAB/$supA_r;
								my $condAB = $supAB/$supB_r;
								push(@{$R->{predict_p_for_o_given_l}}, &new_rule({
									c => $c, p => $p, l => $l,
									ruletype => "predict_p_for_o_given_l",
									patterntype => $patterntype,
									supA => $supA_r, supB => $supB_r, supAB => $supAB,
									condAB => $condAB, condBA => $condBA,
									#entities_for_A => [keys %{$D->{predict_l_for_o_given_p}->{$p}->{A}->{$patterntype}->{$l}}],
									#entities_for_B => [keys %{$D->{predict_l_for_o_given_p}->{$p}->{B}}]
								}));
								#print "found predict_p_for_o_given_l\n";
							} # predict_p_for_o_given_l
						} # foreach l
					} # foreach patterntype
				} # foreach p
			} # predict_l_for_o_given_p, predict_p_for_o_given_l

		} # if file exists
	} # foreach pos


	foreach my $rulepattern (keys %{$CFG->{rulepattern}}){
		if(exists $R->{$rulepattern}){
			my $rulefilename  = "../data/data_per_class/$c/$c-rules-$rulepattern-"
			. join("-",
				$CFG->{min_pattern_frequency},
				$CFG->{max_entities_per_class},
				$CFG->{$rulepattern}->{min_supA},
				$CFG->{$rulepattern}->{min_supB},
				$CFG->{$rulepattern}->{min_supAB}
			) . ".yml"
			;
			print " > $rulefilename (" . (scalar @{$R->{$rulepattern}}) . ") rules\n";
			DumpFile($rulefilename, $R->{$rulepattern});
		}
	}
}

sub new_rule {
	my $d = shift;
	my $r = {};
	foreach my $key (qw(ruletype patterntype c l supA supB supAB condAB condBA entities_for_A entities_for_B s p o)){
		$r->{$key} = $d->{$key} if exists $d->{$key};
	}
	$r->{interestingness} = {
		AllConf		=> $d->{condAB} < $d->{condBA} ? $d->{condAB} : $d->{condBA},
		Coherence	=> 1/(1/$d->{condAB} + 1/$d->{condBA}),
		Cosine		=> sqrt($d->{condAB} * $d->{condBA}),
		Kulczynski	=> ($d->{condAB} + $d->{condBA})/2,
		MaxConf		=> $d->{condAB} < $d->{condBA} ? $d->{condBA} : $d->{condAB},
		IR		=> abs($d->{condAB} - $d->{condBA}) / ($d->{condAB} + $d->{condBA} - $d->{condAB} * $d->{condBA}),
	};
	$r->{CFG} = {
		min_supA                => $CFG->{$d->{ruletype}}->{min_supA},
		min_supB                => $CFG->{$d->{ruletype}}->{min_supB},
		min_supAB               => $CFG->{$d->{ruletype}}->{min_supAB},
		min_class_frequency     => $CFG->{min_class_frequency},
		min_property_frequency  => $CFG->{min_property_frequency},
		min_pattern_frequency   => $CFG->{min_pattern_frequency},
		min_onegram_length      => $CFG->{min_onegram_length},
		max_entities_per_class  => $CFG->{max_entities_per_class},
	};
	return $r
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

