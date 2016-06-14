git tag v1.0 android-studio
git push --tags
git archive android-studio --prefix=steganocc-v1.0/ |gzip > steganocc-v1.0.tar.gz
