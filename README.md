## Btcli4j

![GitHub Workflow Status](https://img.shields.io/github/workflow/status/clightning4j/btcli4j/Java%20CI?style=for-the-badge)

It is a [c-lightning](https://lightning.readthedocs.io/index.html) plugin to override Bitcoin backend plugin with [esplora](https://github.com/Blockstream/esplora) 
powered by [Blockstream](https://blockstream.com/).

But this plugin is designed make the process to run c-lightning with bitcoind in pruning mode, the idea of this plugin was
described inside this [issue by lightningd](https://github.com/lightningd/plugins/issues/112).

It is designed with mediator patter and each command inside the mediator can use esplora or bitcoind or a both in some cases.

So, the plugin will support the complete backend with esplora (only) and the complete backend only with bitcoind or a join of the two option!

## Network supported

- [X] Bitcoin (Mainet (Be cautious) and Testnet)
- [X] Liquid

## Status

The plugin at the moment is under developing, but it should support the esplora backend (on testnet).

If you want test it all feedback are welcome! Feel free to open an issue or a PR or in addition, you can send
email to [vincenzopalazzodev@gmail.com](mailito://vincenzopalazzodev@gmail.com)

## Install
Java produces a jar and c-lightning needs a bash script to run it! 
The gradle script compiles the plugin and generate a bash script with the command `./gradlew createRunnableScript`

After the gradle process, you have the jar inside the `build/libs/btcli4j-all.jar` and the script `btcli4j-gen.sh` 
in the root directory of the project.

Now you can put the gradle script inside the c-lightning plugin directory

### How bash script look like

The contains only the command to run the jar with java, in my cases the script contains the following result>

```bash
#!/bin/bash
/usr/lib/jvm/jdk-13.0.2/bin/java -jar /home/vincent/Github/btcli4j/build/libs/btcli4j-all.jar
```

In this case, you can move this bash script inside your `./lightning/plugins` the directory or you can add the plugin to the file conf
with `plugin=/PATH/bash/file` or use the command line `--plugin=/path/bash/file`.

## Plugin parameter

- btcli4j-proxy: Indicate the proxy address, by default is: "127.0.0.1:9050".
- btcli4j-proxy-enable: A flag option, it help the user to enable the tor socket, by default it is used the same configuration of c-lightning.

A complete example is

```bash
lightningd --disable-plugin bcli
```

The command above, run the lightningd with the lightnind configuration, this mean that you don't need to add some 
additional configuration to the command line.

However, you can use the command line to customize the plugin behaviors.

## Code Style
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

> We live in a world where robots can drive a car, so we shouldn't just write code, we should write elegant code.

This repository use [ktlint](https://github.com/pinterest/ktlint) to maintains the code of the repository elegant, so 
before submit the code check the Kotlin format with the following command on the root of the directory

```bash
./gradlew formatKotlin
```

## Built with

- [JRPClightning](https://github.com/vincenzopalazzo/JRPClightning)

## License

<div align="center">
  <img src="https://opensource.org/files/osi_keyhole_300X300_90ppi_0.png" width="150" height="150"/>
</div>

 It is a c-lightning plugin to override Bitcoin backend plugin with esplora.

 Copyright (C) 2020 Vincenzo Palazzo vincenzopalazzodev@gmail.com
 
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
