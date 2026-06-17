#!/usr/bin/env bash
# Generic 3-way merge for a module. Args: OURS_ROOT BASE_ROOT THEIRS_ROOT
# Merges every file that differs between THEIRS and BASE (Sakura's modifications),
# 3-way against OURS. LF-merge, CRLF write-back. Reports clean/conflict/added.
set -u
OURS="$1"; BASE="$2"; THEIRS="$3"
norm(){ sed 's/\r$//' "$1"; }
tocrlf(){ sed 's/\r$//; s/$/\r/' "$1"; }
TMP=$(mktemp -d)
clean=0; conf=0; added=0; conflist=""
while IFS= read -r rel; do
  rel=${rel#./}
  b="$BASE/$rel"; t="$THEIRS/$rel"; o="$OURS/$rel"
  if [ ! -f "$b" ]; then          # Sakura-added file (not in base) -> copy if missing
    if [ ! -f "$o" ]; then mkdir -p "$(dirname "$o")"; cp "$t" "$o"; added=$((added+1)); fi
    continue
  fi
  diff -q "$b" "$t" >/dev/null 2>&1 && continue   # unchanged by Sakura
  if [ ! -f "$o" ]; then cp "$t" "$o"; added=$((added+1)); continue; fi
  norm "$o" > "$TMP/o"; norm "$b" > "$TMP/b"; norm "$t" > "$TMP/t"
  if git merge-file -q -p --diff3 "$TMP/o" "$TMP/b" "$TMP/t" > "$TMP/m" 2>/dev/null; then
    tocrlf "$TMP/m" > "$o"; clean=$((clean+1))
  else
    tocrlf "$TMP/m" > "$o"; conf=$((conf+1)); conflist="$conflist\n  $(grep -c '^<<<<<<<' "$TMP/m") $rel"
  fi
done < <(cd "$THEIRS" && find . -name '*.java')
echo "CLEAN=$clean CONFLICT=$conf ADDED=$added"
echo -e "conflicts:$conflist"
rm -rf "$TMP"
