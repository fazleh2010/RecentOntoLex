#!/usr/bin/perl -w
use strict;
use YAML::Syck qw(LoadFile DumpFile Load Dump);
use Number::Bytes::Human qw(format_bytes);
use JSON;

# the min values the data has been created with
my $min1 = { min_supA => 50, min_supB => 50, min_supAB => 50 };
# the min values used here to filter
my $min2 = { min_supA => 50, min_supB => 50, min_supAB => 50 };

my $CFG = {
        min_class_frequency 	=> 10000,
        min_property_frequency 	=> 0.005, # better with 0.001, when ready
        min_pattern_frequency 	=> 0.005,
        min_onegram_length 	=> 4,
        max_entities_per_class 	=> 10000,

        rulepattern => {
                predict_l_for_s_given_po        => 1, 
                predict_l_for_s_given_p         => 1, 
                predict_l_for_s_given_o         => 1, 
                predict_l_for_o_given_sp        => 1, 
                predict_l_for_o_given_s         => 1, 
                predict_l_for_o_given_p         => 1, 
                predict_po_for_s_given_l        => 1, 
                predict_p_for_s_given_l         => 1, 
                predict_o_for_s_given_l         => 1, 
                predict_sp_for_o_given_l        => 1, 
                predict_s_for_o_given_l         => 1, 
                predict_p_for_o_given_l         => 1, 
        },

	predict_l_for_s_given_po        => { orig => $min1, new => $min2 },
        predict_l_for_s_given_p         => { orig => $min1, new => $min2 },
        predict_l_for_s_given_o         => { orig => $min1, new => $min2 },
        predict_l_for_o_given_sp        => { orig => $min1, new => $min2 },
        predict_l_for_o_given_s         => { orig => $min1, new => $min2 },
        predict_l_for_o_given_p         => { orig => $min1, new => $min2 },
        predict_po_for_s_given_l        => { orig => $min1, new => $min2 },
        predict_p_for_s_given_l         => { orig => $min1, new => $min2 },
        predict_o_for_s_given_l         => { orig => $min1, new => $min2 },
        predict_sp_for_o_given_l        => { orig => $min1, new => $min2 },
        predict_s_for_o_given_l         => { orig => $min1, new => $min2 },
        predict_p_for_o_given_l         => { orig => $min1, new => $min2 },

        AllConf         => 0.1,
        Coherence       => 0.1,
        Cosine          => 0.1,
        Kulczynski      => 0.1,
        MaxConf         => 0.1,
        IR              => 0.1,

	max_number_of_rules => 1000,
};

my $frequent_class_to_entities_file = "../data/frequent_class_to_entities-" . $CFG->{min_class_frequency} . "-" . $CFG->{max_entities_per_class} . ".yml";
print " < $frequent_class_to_entities_file " . format_bytes(-s $frequent_class_to_entities_file) .  "\n";
my $frequent_class_to_entities = LoadFile($frequent_class_to_entities_file);

my $classes = {};

foreach my $c (sort keys %{$frequent_class_to_entities}){
	foreach my $rulepattern (keys %{$CFG->{rulepattern}}){
		my $rulefilename  = "../data/data_per_class/$c/$c-rules-$rulepattern-"
			. join("-",
				$CFG->{min_pattern_frequency},
				$CFG->{max_entities_per_class},
				$CFG->{$rulepattern}->{orig}->{min_supA},
				$CFG->{$rulepattern}->{orig}->{min_supB},
				$CFG->{$rulepattern}->{orig}->{min_supAB}
			) . ".yml"
		;


		if(-s $rulefilename){
			print " < $rulefilename\n";
               		my $rules = LoadFile($rulefilename);
		
			my $collection = {};
			my $count = {};

			foreach my $rule (@{$rules}){

				$rule->{ruletype} =~ s/\n//; # TODO: remove one that it is not necessary anymore

				next if $rule->{supA} < $CFG->{$rulepattern}->{new}->{min_supA};
                                next if $rule->{supB} < $CFG->{$rulepattern}->{new}->{min_supB};
                                next if $rule->{supAB} < $CFG->{$rulepattern}->{new}->{min_supAB};
				$count->{total}++;

				foreach my $measure (qw(AllConf Coherence Cosine Kulczynski MaxConf IR)){
					if($rule->{interestingness}->{$measure} >= $CFG->{$measure}){
						push(@{$collection->{$measure}->{$rule->{interestingness}->{$measure}}}, $rule);
						$count->{$measure}->{total}++;
					}
				}
			}

			foreach my $measure (keys %{$collection}){
				my $bucket_1 = [];
				my $bucket_2 = [];
				FEV: foreach my $value (reverse sort { $a <=> $b } keys %{$collection->{$measure}}){
					foreach my $rule (@{$collection->{$measure}->{$value}}){
						my $string = &shortPrintRule($rule);
						push(@{$bucket_1}, $string);
						$rule->{as_string} = $string;
                                                push(@{$bucket_1}, $string);
						push(@{$bucket_2}, $rule);
							
                                                if(scalar @{$bucket_1} == $CFG->{max_number_of_rules}){
                                                        last FEV;
                                                }
					}
				}

				my $parameterstring = join("-",
                                        $c,
                                        $rulepattern,
                                        $measure,
                                        $CFG->{min_class_frequency},
                                        $CFG->{min_property_frequency},
                                        $CFG->{min_pattern_frequency},
                                        $CFG->{min_onegram_length},
                                        $CFG->{max_entities_per_class},
                                        $CFG->{$measure},
                                        $CFG->{$rulepattern}->{new}->{min_supA},
                                        $CFG->{$rulepattern}->{new}->{min_supB},
                                        $CFG->{$rulepattern}->{new}->{min_supAB},
                                        $CFG->{max_number_of_rules}
                                );


		                my $filename_HR_yml = "../results/HR_$parameterstring.yml";
				my $filename_MR_yml = "../results/MR_$parameterstring.yml";
				my $filename_HR_json = "../results/HR_$parameterstring.json";
                                my $filename_MR_json = "../results/MR_$parameterstring.json";
				
				my $parameters = {
                                                class => $c,
                                                rulepattern => $rulepattern,
                                                measure => $measure,
                                                threshold => $CFG->{$measure},
                                                min_class_frequency => $CFG->{min_class_frequency},
                                                min_property_frequency => $CFG->{min_property_frequency},
                                                min_pattern_frequency => $CFG->{min_pattern_frequency},
                                                min_onegram_length => $CFG->{min_onegram_length},
                                                max_entities_per_class => $CFG->{max_entities_per_class},
                                                max_number_of_rules => $CFG->{max_number_of_rules},
                                                #total_number_of_rules_of_this_type_for_this_class => $count->{$c}->{$ruletype}->{total},
                                                #total_number_of_rules_of_this_type_for_this_class_above_threshold => $count->{$c}->{$ruletype}->{$measure}->{above_threshold},
                                                min_supA => $CFG->{$rulepattern}->{new}->{min_supA},
                                                min_supB => $CFG->{$rulepattern}->{new}->{min_supB},
                                                min_supAB => $CFG->{$rulepattern}->{new}->{min_supAB},
                                };


				DumpFile($filename_HR_yml, {
                                        _description => $parameters,
                                        rules => $bucket_1,
                                });
				print " > $filename_HR_yml\n"; #<STDIN>;

				DumpFile($filename_MR_yml, {
                                        _description => $parameters,
                                        rules => $bucket_2,
                                });
                                print " > $filename_MR_yml\n"; #<STDIN>;

				my $json_HR = encode_json({
                                        _description => $parameters,
                                        rules => $bucket_1,
                                });
				open(DAT,">$filename_HR_json");
				print DAT $json_HR;
				close DAT;
                                print " > $filename_HR_json\n"; #<STDIN>;

				my $json_MR = encode_json({
                                        _description => $parameters,
                                        rules => $bucket_2,
                                });
                                open(DAT,">$filename_MR_json");
                                print DAT $json_MR;
                                close DAT;
                                print " > $filename_MR_json\n"; #<STDIN>;


				$classes->{$c} = 1;
			}

		} else { print "Rulefilename does not exist: $rulefilename\n"; }
	

	}
}

print "#classes: " . (scalar keys %{$classes}) . "\n";

sub shortPrintRule {
        my $rule = shift;

        my $string = q{};

        if($rule->{ruletype} eq "predict_po_for_s_given_l"){
                $string .= sprintf "{ %s in c_e and occurs('%s', d_e) } => { %s(e, %s, %s) in G }",
			"dbo:" . $rule->{c},
                        $rule->{l},
                        q{},
                        &rewrite_property($rule->{p}),
                        &shorten_object($rule->{o})
                ;
        } elsif($rule->{ruletype} eq "predict_o_for_s_given_l"){ # correct type
                $string .= sprintf "{ %s in c_e and occurs('%s', d_e) } => { %s(e, %s, %s) in G }",
                        $rule->{c},
			$rule->{l},
                        q{exists p : },
                        q{p},
                        &shorten_object($rule->{o})
                ;
        } elsif($rule->{ruletype} eq "predict_p_for_s_given_l"){ # correct type
                $string .= sprintf "{ %s in c_e and occurs('%s', d_e) } => { %s(e, %s, %s) in G }",
                        "dbo:" . $rule->{c},
			$rule->{l},
                        q{exists o : },
                        &rewrite_property($rule->{p}),
                        q{o}
                ;
        } elsif($rule->{ruletype} eq "predict_l_for_s_given_po"){ # correct type
                $string .= sprintf "{ %s in c_e and %s(e, %s, %s) in G } => { occurs('%s', d_e) }",
                        "dbo:" . $rule->{c},
			q{},
                        &rewrite_property($rule->{p}),
                        &shorten_object($rule->{o}),
                        $rule->{l}
                ;
        } elsif($rule->{ruletype} eq "predict_l_for_s_given_o"){ # correct type
                $string .= sprintf "{ %s in c_e and %s(e, %s, %s) in G } => { occurs('%s', d_e) }",
                        "dbo:" . $rule->{c},
			q{exists p: },
                        q{p},
                        &shorten_object($rule->{o}),
                        $rule->{l}
                ;
        } elsif($rule->{ruletype} eq "predict_l_for_s_given_p"){ # correct type
                $string .= sprintf "{ %s in c_e and %s(e, %s, %s) in G } => { occurs('%s', d_e) }",
                        "dbo:" . $rule->{c},
			q{exists o : },
                        &rewrite_property($rule->{p}),
                        q{o},
                        $rule->{l}
                ;
        } elsif($rule->{ruletype} eq "predict_sp_for_o_given_l"){ # correct type
                $string .= sprintf "{ %s in c_e and occurs('%s', d_e) } => { %s(%s, %s, e) in G }",
                        "dbo:" . $rule->{c},
			$rule->{l},
                        q{},
                        &shorten_subject($rule->{s}),
                        &rewrite_property($rule->{p})
                ;
        } elsif($rule->{ruletype} eq "predict_s_for_o_given_l"){ # correct type
                $string .= sprintf "{ %s in c_e and occurs('%s', d_e) } => { %s(%s, %s, e) in G }",
                        "dbo:" . $rule->{c},
			$rule->{l},
                        q{exists p : },
                        &shorten_subject($rule->{s}),
                        q{p}
                ;
        } elsif($rule->{ruletype} eq "predict_p_for_o_given_l"){ # correct type
                $string .= sprintf "{ %s in c_e and occurs('%s', d_e) } => { %s(%s, %s, e) in G }",
                        "dbo:" . $rule->{c},
			$rule->{l},
                        q{exists s : },
                        q{s},
                        &rewrite_property($rule->{p})
                ;
       } elsif($rule->{ruletype} eq "predict_l_for_o_given_sp"){ # correct type
                $string .= sprintf "{ %s in c_e and %s(%s, %s, e) in G } => { occurs('%s', d_e) }",
                        "dbo:" . $rule->{c},
		        q{},
                        &shorten_subject($rule->{s}),
                        &rewrite_property($rule->{p}),
                        $rule->{l}
                ;
        } elsif($rule->{ruletype} eq "predict_l_for_o_given_s"){ # correct type
                $string .= sprintf "{ %s in c_e and %s(%s, %s, e) in G } => { occurs('%s', d_e) }",
                        "dbo:" . $rule->{c},
			q{exists p : },
                        &shorten_subject($rule->{s}),
                        q{p},
                        $rule->{l}
                ;
        } elsif($rule->{ruletype} eq "predict_l_for_o_given_p"){
                $string .= sprintf "{ %s in c_e and %s(%s, %s, e) in G } => { occurs('%s', d_e) }",
                        "dbo:" . $rule->{c},
			q{exists s : },
                        q{s},
                        &rewrite_property($rule->{p}),
                        $rule->{l}
        }

        $string .= " | " . join(", ",
                "supA=" . $rule->{supA},
                "supB=" . $rule->{supB},
                "supAB=" . $rule->{supAB},
                "condBA=" . &shorten_value($rule->{condBA}),
                "condAB=" . &shorten_value($rule->{condAB}),
                "AllConf=" . &shorten_value($rule->{interestingness}->{AllConf}),
                "Coherence=" . &shorten_value($rule->{interestingness}->{Coherence}),
                "Cosine=" . &shorten_value($rule->{interestingness}->{Cosine}),
                "Kulczynski=" . &shorten_value($rule->{interestingness}->{Kulczynski}),
                "MaxConf=" . &shorten_value($rule->{interestingness}->{MaxConf}),
                "IR=" . &shorten_value($rule->{interestingness}->{IR})
        );

        return $string;

}

sub rewrite_ruletype {
        my $t = shift;
        $t =~ s/_/-/g;
        return $t
}

sub rewrite_property {
        my $p = shift;
        if($p =~ m/\Aproperty\/(.*)\Z/){
                return "dbp:$1";
        } elsif($p =~ m/\Aontology\/(.*)\Z/){
                return "dbo:$1";
        } else {
                print "unexpected property name! <$p>\n"; <STDIN>;
        }
}

sub shorten_object {
        my $s = shift;
        if($s =~ m/\A<http:\/\/dbpedia.org\/resource\/(.*)>\Z/){
                return "dbr:$1";
        } else {
                # TODO: in case of datatyped literals, shorten the datatype
                return $s;
        }
}

sub shorten_subject {
        my $s = shift;
        if($s =~ m/\A<http:\/\/dbpedia.org\/resource\/(.*)>/){
                return "dbr:$1";
        }
        return $s;
}

sub shorten_value {
        my $v = shift;
        return (int($v * 1000))/1000;
}
                             

