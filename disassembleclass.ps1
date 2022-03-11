[CmdletBinding()]
param (
    # Name of class to disassemble
    [Parameter()]
    [string]
    $ClassName
)

$JAR_PATHS = @(".gradle/loom-cache")

$ClassPathParameters = @()
foreach ($JarPath in $JAR_PATHS) {
    $ClassPathParameters += Get-ChildItem -Path $JarPath -Recurse -Include *.jar
}
$ClassPathParameters = $ClassPathParameters -join ";"

javap.exe -classpath $ClassPathParameters -c -p -s $ClassName > "bytecode/$ClassName"
