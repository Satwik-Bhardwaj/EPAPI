#!/bin/bash
echo '上传数据库文件 '`date +%H:%m:%S`
sshpass -p 'tHDGHA88@#' scp build/libs/entpack.sql lucky@148.72.215.110:/home/lucky/
sshpass -p 'tHDGHA88@#' ssh lucky@148.72.215.110 'cd /home/lucky &&
./impMysqlMember.sh'
echo '完成导入'`date +%H:%m:%S`
exit 0