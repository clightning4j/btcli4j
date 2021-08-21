## Btcli4j

![GitHub Workflow Status](https://img.shields.io/github/workflow/status/clightning4j/btcli4j/Java%20CI?style=flat-square)
![Esplora support](https://img.shields.io/badge/esplora-supported-gren?style=flat-square)
![Pruning mode](https://img.shields.io/badge/prune-supported-gren?style=flat-square)

It is a [c-lightning](https://lightning.readthedocs.io/index.html) plugin to override Bitcoin backend plugin with [esplora](https://github.com/Blockstream/esplora) 
powered by [Blockstream](https://blockstream.com/).

But this plugin is designed make the process to run c-lightning with bitcoind in pruning mode, the idea of this plugin was
described inside this [issue by lightningd](https://github.com/lightningd/plugins/issues/112).

It is designed with mediator patter and each command inside the mediator can use esplora or bitcoind or a both in some cases.

So, the plugin will support the complete backend with esplora (only) and the complete backend only with bitcoind or a join of the two option!

## Network supported

- [X] Bitcoin (Mainet (Be cautious) and Testnet)
- [X] Liquid
- [X] Bitcoin Core pruning mode (Mainet (Be cautious) and Testnet)
- [X] Bitcoin Core remote call (have bitcoin in another location of your lightningd node) (Mainet (Be cautious) and Testnet)
- [ ] Litecoin
- [ ] Liquid

## Status

The plugin is in Beta stage, this mean that is waiting for the User Review and testing, good tested on bitcoin testnet
but not on mainet, because I have put inside only small amount of found that available at the moment, if you want support 
the project consider to look in the section [Support]()

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

- btcli4j-endpoint: A string option that is the end point url,by default it is the [esplora](https://github.com/Blockstream/esplora/blob/master/API.md) end point, 
- it is the end point it is different the proxy with tor is lost from the automatic configuration of the plugin
- btcli4j-waiting: A integer that is the base waiting time where the plugin will apply the increase waiting time in case of error with the end point.
- btcli4j-proxy: Indicate the proxy address, by default is: "127.0.0.1:9050".
- btcli4j-proxy-enable: A flag option, it helps the user to enable the tor socket, by default it is used the same configuration of c-lightning.
- btcli4j-pruned: A flag option that tell to the plugin to ran in pruning mode
- bitcoin-rpcurl: A string option that is the url of Bitcoin RPC interface, usually `http://127.0.0.1:8332` for mainet and `http://127.0.0.1:18332` for testnet
- bitcoin-rpcuser: A string option that is the user to make the login in Bitcoin core
- bitcoin-rpcpassword: A string option that is the password to make the login in Bitcoin core

P.S: some of Bitcoin propriety are the same of the C-lightning propriety, these mean that yuo need to redefine only a ``bitcoin-rpcurl`` propriety, and put
at the startup of the lightning node the flag `btcli4j-pruned`

### Esplora API config

A complete example to run with Esplora API is reported below

```bash
lightningd --disable-plugin bcli
```

The command above, run the lightningd with the lightnind configuration, this mean that you don't need to add some 
additional configuration to the command line.

However, you can use the command line to customize the plugin behaviors.

### Bitcoin Pruning mode config

```bash
lightningd --disable-plugin bcli --btcli4j-pruned
```

With the following clightning config

```bash
...
bitcoin-rpcuser=user
bitcoin-rpcpassword=pass
bitcoin-rpcurl=http://127.0.0.1:18332
....
```

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
- [lite-bitcoin-rpc](https://github.com/clightning4j/lite-bitcoin-rpc)

## Support
If you like the library and want to support it, please considerer to donate with the following system


- [liberapay.com/vincenzopalazzo](https://liberapay.com/vincenzopalazzo)
- [3BQ8qbn8hLdmBKEjt1Hj1Z6SiDsnjJurfU](bitcoin:3BQ8qbn8hLdmBKEjt1Hj1Z6SiDsnjJurfU)
- [Github support](https://github.com/sponsors/vincenzopalazzo)

## License

<div align="center">
  <img src="https://opensource.org/files/osi_keyhole_300X300_90ppi_0.png" width="150" height="150"/>
</div>

 It is a c-lightning plugin to override Bitcoin backend plugin with esplora.

 Copyright (C) 2020-2021 Vincenzo Palazzo vincenzopalazzodev@gmail.com
 
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
