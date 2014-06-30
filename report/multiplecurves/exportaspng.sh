#!/bin/bash
# Create png file for all PDFs, file.pdf -> file.png
for f in *.pdf ; do convert -density 300 -trim -resize 600x -alpha remove "$f" "${f%.pdf}.png" ; done
