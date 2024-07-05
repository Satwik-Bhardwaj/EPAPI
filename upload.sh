#!/bin/bash
echo '部署到NUC '`date +%H:%m:%S`
sshpass -p 'tHDGHA88@#' scp target/entpack.jar lucky@148.72.215.110:/home/lucky/entpack
sshpass -p 'tHDGHA88@#' ssh lucky@148.72.215.110 'cd /home/lucky/entpack && ./start.sh'
echo '部署完成'`date +%H:%m:%S`
exit 0