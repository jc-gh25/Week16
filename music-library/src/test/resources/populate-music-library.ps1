<# ---------------------------------------------------------------
   Music Library API – Sample Data Population (Windows PowerShell 5.1)
   --------------------------------------------------------------- #>

param(
    [string]$BaseUrl = "http://localhost:8080"
)

# ----------------------------------------------------------------
# Disable TLS/SSL validation for this session (INSECURE!)
# ---------------------------------------
Add-Type @"
using System.Net;
using System.Security.Cryptography.X509Certificates;
public class TrustAllCertsPolicy : ICertificatePolicy {
    public bool CheckValidationResult(ServicePoint srvPoint, X509Certificate certificate,
        WebRequest request, int certificateProblem) { return true; }
}
"@
[System.Net.ServicePointManager]::CertificatePolicy = New-Object TrustAllCertsPolicy
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12

param(
    [string]$BaseUrl = "http://localhost:8080"
)

function Invoke-Api {
    param(
        [ValidateSet('GET','POST','PUT','DELETE','PATCH')]
      [string]$Method,
        [string]$Url,
        [hashtable]$Body = $null
    )

    $params = @{
        Uri                  = $Url
        Method               = $Method
        Headers              = @{ "Content-Type" = "application/json" }
        SkipCertificateCheck = $true          # <-- ignore Railway TLS cert
        ErrorAction          = 'Stop'
    }

    if ($Body) { $params.Body = ($Body | ConvertTo-Json -Depth 10) }

    try {
        Invoke-RestMethod @params
    } catch {
        Write-Host "❌ $Method $Url failed: $_" -ForegroundColor Red
    }
}

# ----------------------------------------------------------------
# Normalise the base URL (remove a trailing slash if present)
# -----------------------------------------------------------
if ($BaseUrl.EndsWith('/') -or $BaseUrl.EndsWith('\')) {
    $BaseUrl = $BaseUrl.Substring(0,$BaseUrl.Length-1)
}
$ApiUrl = "$BaseUrl/api"

Write-Host "`n=== Music Library API – Data Population ===`n" -ForegroundColor Cyan
Write-Host "Target API: $ApiUrl`n"

# ------------------- GENRES --------------------
Write-Host ">>> Step 1: Creating Genres..."
$genres = @(
    @{ name="Rock";        description="Rock music is a broad genre of popular music that originated as rock and roll in the United States in the late 1940s and early 1950s." },
    @{ name="Pop";         description="Pop music is a genre of popular music that originated in its modern form during the mid‑1950s." },
    @{ name="Jazz";        description="Jazz is a music genre that originated in the African‑American communities of New Orleans in the late 19th and early 20th centuries." },
    @{ name="Blues";       description="Blues is a music genre and musical form which originated in the Deep South of the United States around the 1860s." },
    @{ name="Electronic";  description="Electronic music is music that employs electronic musical instruments, digital instruments, or circuitry‑based music technology." }
)
foreach($g in $genres){
    Invoke-Api -Method POST -Url "$ApiUrl/genres" -Body $g
    Write-Host "✅ $($g.name)"
}

# ------------------- ARTISTS -------------------
Write-Host "`n>>> Step 2: Creating Artists..."
$artists = @(
    @{ name="The Beatles";          description="The Beatles were an English rock band formed in Liverpool in 1960. Regarded as the most influential band of all time." },
    @{ name="The Rolling Stones";   description="The Rolling Stones are an English rock band formed in London in 1962. Active for six decades." },
    @{ name="Pink Floyd";           description="Pink Floyd are an English rock band formed in London in 1965. Distinguished for their extended compositions and sonic experimentation." },
    @{ name="Queen";                description="Queen are a British rock band formed in London in 1970. Classic line‑up was Freddie Mercury, Brian May, Roger Taylor and John Deacon." },
    @{ name="Led Zeppelin";         description="Led Zeppelin were an English rock band formed in London in 1968." },
    @{ name="Nirvana";              description="Nirvana was an American rock band formed in Aberdeen, Washington, in 1987." },
    @{ name="Radiohead";            description="Radiohead are an English rock band formed in Abingdon, Oxfordshire, in 1985." },
  @{ name="Daft Punk";            description="Daft Punk were a French electronic music duo formed in 1993 in Paris." },
    @{ name="Miles Davis";          description="Miles Dewey Davis III was an American trumpeter, bandleader, and composer." },
  @{ name="Taylor Swift";         description="Taylor Alison Swift is an American singer‑songwriter." }
)
foreach($a in $artists){
    Invoke-Api -Method POST -Url "$ApiUrl/artists" -Body $a
    Write-Host "✅ $($a.name)"
}

# ------------------- ALBUMS --------------------
Write-Host "`n>>> Step 3: Creating Albums (IDs assumed sequential)…"
# NOTE: This assumes the IDs are 1‑10 for artists and 1‑5 for genres.
$albums = @(
    @{ title="Abbey Road";                     releaseDate="1969-09-26"; artistId=1; genreIds=@(1) },
    @{ title="Sgt. Pepper's Lonely Hearts Club Band"; releaseDate="1967-06-01"; artistId=1; genreIds=@(1,2) },
    @{ title="Sticky Fingers";                 releaseDate="1971-04-23"; artistId=2; genreIds=@(1,4) },
    @{ title="The Dark Side of the Moon";      releaseDate="1973-03-01"; artistId=3; genreIds=@(1) },
    @{ title="The Wall";                       releaseDate="1979-11-30"; artistId=3; genreIds=@(1) },
    @{ title="A Night at the Opera";           releaseDate="1975-11-21"; artistId=4; genreIds=@(1) },
    @{ title="Led Zeppelin IV";                releaseDate="1971-11-08"; artistId=5; genreIds=@(1) },
    @{ title="OK Computer";                    releaseDate="1997-05-21"; artistId=7; genreIds=@(1) },
    @{ title="Discovery";                      releaseDate="2001-03-12"; artistId=8; genreIds=@(5,2) },
    @{ title="Random Access Memories";          releaseDate="2013-05-17"; artistId=8; genreIds=@(5,2) },
    @{ title="Kind of Blue";           releaseDate="1959-08-17"; artistId=9; genreIds=@(3) },
    @{ title="1989";                           releaseDate="2014-10-27"; artistId=10; genreIds=@(2) },
    @{ title="Folklore";                       releaseDate="2020-07-24"; artistId=10; genreIds=@(2) }
)
foreach($al in $albums){
    Invoke-Api -Method POST -Url "$ApiUrl/albums" -Body $al
    Write-Host "✅ $($al.title)"
}

Write-Host "`n=== Population Complete! ===`n"
Write-Host "Browse: $BaseUrl"
Write-Host "Endpoints:"
Write-Host "  GET $ApiUrl/artists"
Write-Host "  GET $ApiUrl/albums"
Write-Host "  GET $ApiUrl/genres"
