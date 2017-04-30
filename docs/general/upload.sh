#scp index.html ptitmain@scm.gforge.inria.fr:/home/groups/steganocc/htdocs
generate-md --layout mixu-gray --input index.md --output out
scp -r images out/* ptitmain@scm.gforge.inria.fr:/home/groups/steganocc/htdocs
