##### Linux无法显示网卡IP导致无法连接虚拟机问题：

> ```shell
> #关闭掉网络管理
> systemctl stop NetworkManager
> 
> #将网络管理禁用
> systemctl disable NetworkManager
> 
> #重启网络
> service network restart
> ```

##### Linux无法显示共享文件夹问题：

> ```shell
> #终端输入：
> vmware-hgfsclient
> 
> #查询已经设置的共享文件，当前应该返回的是CentoseShare,因为我把路径设置的是D:/CentoseShare，你们会返回你们对应的
> 
> #终端输入：
> vmhgfs-fuse .host:/CentoseShare /mnt/hgfs
> 
> #没有报错即为成功，占到/mnt/hgfs目录下发现成功挂载，已经能看到对应windows操作系统的共享文件了但是重启发现还需要再操作一次，非常麻烦。
> #为了解决上述重启需要重新挂载的问题，需要改写从/etc/fstab文件
> 
> #终端输入：
> vi /etc/fstab
> 
> #进入fstab文件的阅读模式,终端输入：i进入编辑模式，此时就能编辑，移动光标上下左右，移动到最后一行.然后加上：
> .host:/CentoseShare /mnt/hgfs fuse.vmhgfs-fuse allow_other 0 0
> 
> #然后按Esc返回阅读模式，再输入：wq保存退出(注意这里包含：)
> 
> #这时候还需要重启验证一下
> reboot
> ```
>
> 



#####  Centos安装 Docker

```shell
## Centos安装 Docker
#移除旧的版本：
yum remove docker docker-client docker-client-latest docker-common docker-latest docker-latest-logrotate docker-logrotate docker-selinux docker-engine-selinux docker-engine

#安装一些必要的系统工具：
yum install -y yum-utils device-mapper-persistent-data lvm2

#添加软件源信息：
yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

#更新 yum 缓存：
# centos 7
yum makecache fast
# CentOS 8没有fast这个命令
yum makecache

#安装 Docker-ce：
yum -y install docker-ce

#查看已安装docker版本
docker version

#启动 Docker 后台服务
systemctl start docker

#开机启动
systemctl enable docker

#新版的 Docker 使用 `/etc/docker/daemon.json`（Linux，没有请新建）。
/etc/docker/daemon.json

#请在该配置文件中加入：
#（没有该文件的话，请先建一个）
{
  "registry-mirrors": ["https://7qyk8phi.mirror.aliyuncs.com"]
}

#重启docker
systemctl daemon-reload
systemctl restart docker

#检查加速器是否生效
docker info

#下载docker-compose
curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

#对二进制文件应用可执行权限
chmod +x /usr/local/bin/docker-compose

#测试安装,若有docker-compose version 1.29.2, build 5becea4c，则安装成功
docker-compose --version
```



##### 通过docker-compose.yaml文件快速安装Mysql、Nacos、Seata、Redis、Elasticsearch、Kibana、Canal、Rocketmq中间件

> 文件名：docker-compose.yaml
>
> ```shell
> #在要复制文件的上一级输入，将docker-compse文件复制到root/docker文件夹路径下
> cp -r docker-compse /root/docker
> 
> #执行授权命令
> chmod -R 777 ./rocketmq/broker/logs
> chmod -R 777 ./rocketmq/broker/store
> chmod -R 666 ./minio/data
> chmod -R 777 ./elasticsearch/data
> 
> #启动容器，容器一共要下载7G左右的数据，需要耐心等待
> docker-compose up -d --build
> 
> #各个端访问路径及其密码
> 
> 
>  - mysql: 使用数据库连接工具进行连接， 端口3306 账号及密码 root/root
>  - minio: http://192.168.0.86:9000 账号及密码 admin/admin123456
>  - redis: 端口6379，没有密码
>  - nacos: http://192.168.0.86:8848/nacos 账号及密码 nacos/nacos
>  - kibana: http://192.168.0.86:5601
>  - rocketmq-dashboard：http://192.168.0.86:8180
> ```





> ```yaml
> version: "3.5"
> 
> services:
> mall4cloud-mysql:
>  image: mysql:8.0
>  container_name: mall4cloud-mysql
>  restart: always
>  environment:
>       - MYSQL_ROOT_PASSWORD=root
>     ports:
>       - 3306:3306
>     volumes:
>       - ./mysql/data:/var/lib/mysql
>       - ./mysql/conf.d:/etc/mysql/conf.d
>       - ./mysql/initdb:/docker-entrypoint-initdb.d
> 
>   mall4cloud-minio:
>     image: minio/minio:RELEASE.2021-06-17T00-10-46Z
>     container_name: mall4cloud-minio
>     restart: always
>     command: server /data
>     ports:
>       - 9000:9000
>     volumes:
>       - ./minio/data:/data
>     environment:
>       - MINIO_ROOT_USER=admin
>       - MINIO_ROOT_PASSWORD=admin123456
> 
>   mall4cloud-redis:
>     image: redis:6.2
>     container_name: mall4cloud-redis
>     restart: always
>     ports:
>       - 6379:6379
> 
> 
>   mall4cloud-nacos:
>     image: nacos/nacos-server:v2.2.0-slim
>     container_name: mall4cloud-nacos
>     restart: always
>     depends_on:
>       - mall4cloud-mysql
>     ports:
>       - 8848:8848
>       - 9848:9848
>       - 9849:9849
>     environment:
>       - JVM_XMS=256m
>       - JVM_XMX=256m
>       - MODE=standalone
>       - PREFER_HOST_MODE=hostname
>       - SPRING_DATASOURCE_PLATFORM=mysql
>       - MYSQL_SERVICE_HOST=192.168.0.86
>       - MYSQL_SERVICE_DB_NAME=mall4cloud_nacos
>       - MYSQL_SERVICE_USER=root
>       - MYSQL_SERVICE_PASSWORD=root
>     volumes:
>       - ./nacos/logs:/home/nacos/logs
> 
> 
>   mall4cloud-seata:
>     image: seataio/seata-server:1.6.1
>     container_name: mall4cloud-seata
>     restart: always
>     depends_on:
>       - mall4cloud-mysql
>       - mall4cloud-nacos
>     ports:
>       - 8091:8091
>       - 7091:7091
>     environment:
>       - SEATA_IP=192.168.0.86
>     volumes:
>       - ./seata/application.yml:/seata-server/resources/application.yml
> 
>   mall4cloud-elasticsearch:
>     image: elasticsearch:7.17.5
>     container_name: mall4cloud-elasticsearch
>     restart: always
>     ports:
>       - 9200:9200
>       - 9300:9300
>     environment:
>       - discovery.type=single-node
>       - ES_JAVA_OPTS=-Xms512m -Xmx512m
>     volumes:
>       - ./elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml
>       - ./elasticsearch/data:/usr/share/elasticsearch/data
>       - ./elasticsearch/plugins:/usr/share/elasticsearch/plugins
> 
>   mall4cloud-kibana:
>     image: kibana:7.17.5
>     container_name: mall4cloud-kibana
>     restart: always
>     ports:
>       - 5601:5601
>     links:
>       - mall4cloud-elasticsearch:elasticsearch
>     depends_on:
>       - mall4cloud-elasticsearch
> 
>   mall4cloud-canal:
>     image: canal/canal-server:v1.1.6
>     container_name: mall4cloud-canal
>     restart: always
>     ports:
>       - 11111:11111
>     volumes:
>       - ./canal/conf/example:/home/admin/canal-server/conf/example
>       - ./canal/conf/canal.properties:/home/admin/canal-server/conf/canal.properties
>       - ./canal/logs:/home/admin/canal-server/logs
> 
> 
>   mall4cloud-rocketmq-namesrv:
>     image: apache/rocketmq:4.9.4
>     container_name: mall4cloud-rocketmq-namesrv
>     restart: always
>     ports:
>       - 9876:9876
>     volumes:
>       - ./rocketmq/namesrv/logs:/home/rocketmq/logs
>       - ./rocketmq/namesrv/store:/home/rocketmq/store
>     environment:
>       JAVA_OPT_EXT: "-Duser.home=/home/rocketmq -Xms512M -Xmx512M -Xmn128M"
>     command: ["sh","mqnamesrv"]
>     networks:
>       rocketmq:
>         aliases:
>           - mall4cloud-rocketmq-namesrv
> 
>   mall4cloud-rocketmq-broker:
>     image: apache/rocketmq:4.9.4
>     container_name: mall4cloud-rocketmq-broker
>     restart: always
>     ports:
>       - 10909:10909
>       - 10911:10911
>     volumes:
>       - ./rocketmq/broker/logs:/home/rocketmq/logs
>       - ./rocketmq/broker/store:/home/rocketmq/store
>       - ./rocketmq/broker/conf/broker.conf:/etc/rocketmq/broker.conf
>     environment:
>       JAVA_OPT_EXT: "-Duser.home=/home/rocketmq -Xms512M -Xmx512M -Xmn128M -XX:-AssumeMP"
>     command: ["sh","mqbroker","-c","/etc/rocketmq/broker.conf","-n","mall4cloud-rocketmq-namesrv:9876","autoCreateTopicEnable=true"]
>     depends_on:
>       - mall4cloud-rocketmq-namesrv
>     networks:
>       rocketmq:
>         aliases:
>           - mall4cloud-rocketmq-broker
> 
> 
>   mall4cloud-rocketmq-dashboard:
>     image: apacherocketmq/rocketmq-dashboard:1.0.0
>     container_name: mall4cloud-rocketmq-dashboard
>     restart: always
>     ports:
>       - 8180:8080
>     environment:
>       JAVA_OPTS: "-Drocketmq.namesrv.addr=mall4cloud-rocketmq-namesrv:9876 -Dcom.rocketmq.sendMessageWithVIPChannel=false"
>     depends_on:
>       - mall4cloud-rocketmq-namesrv
>     networks:
>       rocketmq:
>         aliases:
>           - mall4cloud-rocketmq-console
> 
> 
> networks:
>   rocketmq:
>     name: rocketmq
>     driver: bridge
> 
> ```
>
> 

```
firewall-cmd --zone=public --add-port=8848/tcp --permanent

rpm -ev mysql-community-libs-5.7.44-1.el7.x86_64 --nodeps
```

修改Linux防火墙时需要重启Docker服务



##### Docker-compose如果重复Build会发生什么？

> 如果修改过Docker-compose中service的配置，则会重新生成一个更新配置之后的镜像
>
> 如果没有修改过Docker-compose中service的配置，则不会改变镜像

##### idea如何全局查找并替换？

> Ctrl+Shift+R