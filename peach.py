#!/usr/bin/env python
"""
Strip the package declarations and fix the imports because Peach
does not accept Java files in packages.

See inputdir and outputdir variables for the paths relative to this file.
"""
import os
import sys
import re

root = os.path.dirname(os.path.realpath(__file__))
inputdir = os.path.join(root, 'src')
outputdir = os.path.join(root, 'submit_to_peach')
excludedirs = ['viewer']

if not os.path.isdir(inputdir):
    sys.stderr.write("Input directory does not exist: %s\n" % inputdir)
    sys.exit(1)

if not os.path.isdir(outputdir):
    os.makedirs(outputdir)

# Remove contents of directory
for dirpath, _, filenames in os.walk(outputdir):
    for filename in filenames:
        os.unlink(os.path.join(dirpath, filename))


def importreplacer(m):
    """
    Strip the package from the import identifier
    match.group(1) = "import "
    match.group(2) = "tue. ..."
    """
    identifier = m.group(2).split('.')
    # Try to find the longest path prefix
    for index in reversed(range(1, len(identifier))):
        importpath = os.path.join(*identifier[0:index])
        importpath = os.path.join(inputdir, importpath)
        if os.path.isdir(importpath):
            identifier = identifier[index:]
            if len(identifier) <= 1:
                # A single identifier (file). Just comment out the import
                return '//' + m.group(0)
            # More than one part. Probably an inner class.
            # Classes cannot be imported from the default package, so they
            # need to be put in a separate package
            sys.stderr.write('WARNING: Put the following inner classes in a ')
            sys.stderr.write('separate .java file %s\n' % m.group(2))
            # Import without package prefix.
            return m.group(1) + '.'.join(identifier) + '; //' + m.group(2)
    return m.group(0)

# Write stripped contents to directory
for dirpath, dirs, filenames in os.walk(inputdir, topdown=True):
    dirs[:] = [d for d in dirs if d not in excludedirs]
    for filename in filenames:
        if not filename.lower().endswith('.java'):
            continue

        with open(os.path.join(dirpath, filename), 'r') as file:
            content = file.read()

        # Comment out package statements and imports
        content = re.sub(r'^\s*package ', r'//\g<0>',
                         content, count=1, flags=re.MULTILINE)

        content = re.sub(r'^([\t ]*import )(tue\.[^; \t]+)', importreplacer,
                         content, flags=re.MULTILINE)

        outputfilepath = os.path.join(outputdir, filename)
        if os.path.isfile(outputfilepath):
            sys.stderr.write("Warning: Duplicate file name %s\n" % filename)

        # Write content to the output directory
        with open(outputfilepath, 'w') as file:
            file.write(content)
