use warnings;
use strict;
use Encode;
use utf8;
use open IO => ':encoding(utf8)';
use ElectricCommander;
use Switch;

$| = 1;

# Read procedure parameter(s)
my $formalLevel = q{$[formalLevel]};

# Create ElectricCommander instance
my $ec = new ElectricCommander();
$ec->abortOnError(0);

my $msg = 'Hello World!';
# Change the message based on the formal level selected
switch ($formalLevel){
   case("usual") { $msg = "Hello World!"; }
   case("surfer") { $msg = "Yo, World!"; }
   case("elegant") { $msg = "Greetings World!"; }
}

# Set the message in the job summary as well as print it in the step logs.
$ec->setProperty("summary", $msg . "\n");
print $msg;

exit(0);
