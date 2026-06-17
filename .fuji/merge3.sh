#!/usr/bin/env bash
# Fuji 3-way merge driver. LF-normalize ours/base/theirs, merge, write back as CRLF.
# Usage: merge3.sh <file-list>   (paths relative to leaf-server/src/minecraft/java)
set -u
cd "$(dirname "$0")/.."   # -> Fuji root
FUJI=$(pwd)
OURS="$FUJI/leaf-server/src/minecraft/java"
BASE="/d/BadgersMC-Dev/Sakura-donor/.gradle/caches/paperweight/upstreams/server-work/paper/src/minecraft/java"
THEIRS="/d/BadgersMC-Dev/Sakura-donor/sakura-server/src/minecraft/java"
TMP="$FUJI/.fuji/m"; mkdir -p "$TMP"
: > "$FUJI/.fuji/clean.txt"; : > "$FUJI/.fuji/conflict.txt"; : > "$FUJI/.fuji/skipped.txt"
norm(){ sed 's/\r$//' "$1"; }            # CRLF/CR -> LF
tocrlf(){ sed 's/\r$//; s/$/\r/' "$1"; } # normalize then CRLF
while read -r f; do
  [ -z "$f" ] && continue
  o="$OURS/$f"; b="$BASE/$f"; t="$THEIRS/$f"
  if [ ! -f "$o" ] || [ ! -f "$b" ] || [ ! -f "$t" ]; then echo "$f" >> "$FUJI/.fuji/skipped.txt"; continue; fi
  norm "$o" > "$TMP/o"; norm "$b" > "$TMP/b"; norm "$t" > "$TMP/t"
  if git merge-file -q -p --diff3 "$TMP/o" "$TMP/b" "$TMP/t" > "$TMP/merged" 2>/dev/null; then
    tocrlf "$TMP/merged" > "$o"; echo "$f" >> "$FUJI/.fuji/clean.txt"
  else
    nc=$(grep -c '^<<<<<<<' "$TMP/merged")
    tocrlf "$TMP/merged" > "$o"      # write with markers (CRLF) for in-place resolve
    echo "$nc $f" >> "$FUJI/.fuji/conflict.txt"
  fi
done < "$1"
echo "CLEAN: $(wc -l < "$FUJI/.fuji/clean.txt")  CONFLICT: $(wc -l < "$FUJI/.fuji/conflict.txt")  SKIPPED: $(wc -l < "$FUJI/.fuji/skipped.txt")"
echo "--- conflicts (worst first) ---"; sort -rn "$FUJI/.fuji/conflict.txt"
