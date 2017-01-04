# ![][logo] Tunnel Tool

Tunnel Tool is a small tool helping to access remote servers. Features:

  - Open SSH Tunnels to various servers
  - Download Logfiles
  - Open Database Connections
  - Upload Files to Servers
  
## Build and Run
Checkout the project and use:
```sh
$ mvn package
```
Executable files will be created at /target/jfx/native

## Settings
To avoid hardcoding of different connections, scripts can be defined via "File -> Settings"
#### Database Settings
Example configuration entry: "Our special QS Database"
```sh
@echo off
start /min plink.exe -l username -P 22 -pw password -L 1111:myqsserver:22 testserverbetween
start /min plink.exe -l username -P 1111 -pw password -L 1550:mydbserver:1560 localhost
```
 
#### SCP Tunnel Scripts
Example configuration entry: "Our special QS File Access"
```sh
@echo off
start /min plink.exe -l username -P 22 -pw password -L 1122:myqsserver:22 testserverbetween
winscp.exe sftp://username:password@localhost:1122/my/target/files/folder/
taskkill /IM plink.exe
 ```
 
#### Misc Settings
SQL Developer path, 
```sh
eg. "C:\sqldeveloper\sqldeveloper.exe"
```

Temp File Upload Script example:
```sh
@echo off
start /min plink.exe -l username -P 22 -pw password -L 1122:myqsserver:22 testserverbetween
ping 127.0.0.1 -n 3 > nul
winscp.exe sftp://username:password@localhost:1122/tmp/ /upload %FILENAME%
putty.exe -ssh username@localhost -P 1122 -pw password -m %PUTTYFILE%
taskkill /IM plink.exe
```

Prod File Movement Script example:
```sh
echo "cp /tmp/%FILENAME% /my/target/folder/%FILENAME% ; /bin/bash" | sudo /bin/su - userToUse
```

___
##### Licenses
   This software contains plink.exe ([PuTTY License]) and winscp.exe ([WinSCP License]). 
   All rights of these applications belong to their creators. 
   They are not covered by the license used by TunnelTool.
   
   App icon made by [Freepik] from [Flaticon]


[logo]: https://raw.githubusercontent.com/DaHu4wA/tunneltool/master/src/main/resources/appicon48.png "TunnelTool Logo"

[PuTTY License]: <http://www.chiark.greenend.org.uk/~sgtatham/putty/licence.html>
[WinSCP License]: <https://winscp.net/eng/docs/license>
[Freepik]: <http://www.freepik.com>
[Flaticon]: <http://www.flaticon.com>

