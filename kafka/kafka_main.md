# Kafka 端到端源码解析

## Topic创建
   zk注册，controller选举具体的数据结构与流程
   
   
## Topic状态流转
   创建、在线、增加分区、下线、删除
   



### Topic分区初始化选择

  按照broker数量均匀地分布在每个broker上

## kafka producer解析

### 1. 发送流程
* 第一步： 刷新元数据
* 第二步： 序列化、选择分区、注册拦截器回调函数
* 第三步： 往RecordAccmulator发送数据
* 第四步：判断batch是否满了，满了的话唤醒send后台线程 <br>
  有可能的异常：API版本不匹配；Buffer耗尽等

### 2. 分区选择策略？
*  若该消息内无指定分区，则使用消息中指定的key哈希生成的分区
*  若key为null，则按照轮询的方式生成分区
*  最后一种，若仍然不满足需求，用户还可以自己指定partition分区策略类，每条消息都按照这个策略进行  <br>
    因此，分区策略可以有四个级别：用户自定义分区策略类、key哈希、轮询、任一消息选择任一分区，总的来说给用户很大的自由度。


### 3. 拦截器有什么作用？
在每次消息处理前增加一个回调函数，一般用来记录一些统计信息，为每条消息增加其他字段等等。

### 4. 关键数据结构
RecordAccmulator的内部是如何运作的？

《TopicPartition，Batch队列》这是重要的数据结构

有一个缓冲池bufferPool，每次开始是已经有batch在发，如果不存在则开辟batchSize大小的空间；然后往Batch队列的append数据，并且使得offset+1,然后会生成一个FutureRecordMetadata，用来表示batch是否满

### 5.  参数配置
1. batch.size指的是大小，不是消息数
2. ling.ms是每隔该时间就定时发送
3.  maxFlightPerConnection=1保证了消息在单分区内的顺序性

### 6. ACK机制
 代表对于消息可靠性的容忍度 
 
### 7. Producer一些问题
*  如何保证topic消息顺序性？
*  性能调优问题？
*   数据压缩问题？
*   数据幂等性？
 


## Kafka网络接收层
###  Kafka channel
###  如何做限流的


## Kafka内存管理

### 堆内存

### 堆外内存
ByteBufferMessageSet 解读
 

## kafka 存储层解析

### 消息格式


###  消息索引

1. **给定时间戳—>定位某个LogSegment—>定位offset—>定位消息位置?** <br>
根据时间戳查找offset，先顺序定位到LogSegment（找到第一个大于该时间戳的LogSegment),然后timeindex内部二分查找定位到offset
2.  **给定offset—> 定位到某个LogSegment—>定位消息位置 ?** <br>
 根据offset，跳表中定位到LogSegment,然后index内部二分查找定位到offset位置，再顺序搜索定位到文件位置
 
## 副本管理
为什么用ISR，不用Raft之类的协议？借鉴了PacificA算法协议。 两个重要的组件：配置管理（对应kafka ISR，leader epoch commited_point) <br>
==HighWaterMark的作用：commited 消息度量；读可见性==
参考http://www.thinkingyu.com/articles/PacificA/ 


### failover机制

*  若unclean.leader.election.enable为true，再去replica中去找存活的broker。而ISR中的broker存在是这样：只有当follower从leader拉取数据跟得上leader的数据速度时，才会在ISR中，否则，被剔除掉ISR列表中。
*  若unclean.leader.election.enable为false，抛出异常

为什么会有unclean.leader.election.enable这个参数呢？

那么数据一致性是如何保证的呢，如何知道副本的状态是可靠的？ISR就保存了kafka认为可靠的副本，它们具备这样的条件：1 . 落后leader的消息条数在一定阈值内 2.或者落后在一定时间内；
但是，follower的复制状态谁又能保证一定能跟得上leader呢？这样，就存在着一种可能性，有可能ISR中只有leader,其他的副本都跟不上leader; 因此，这个时候，patition到底可用不可用？这就是一个权衡了，若只从ISR中获取leader，保证了数据的可靠性，但partition就不可用了，若从replica中获取，则可用性增强，但是数据可能存在丢失情况。
因此unclean.leader.election.enable这个参数设计为true，则保证了可用性，也就是CAP中的A P;设置为false，则保证了数据一致性，也就是CAP中的CP

## kafka Consumer解析
现有的kafka可以做到写幂等性（0.11版本之后），但是做不到消费幂等性。消费完后写offset到zk失败，这个状态consumer客户端是感知不到的，二者并没有类似TCP的ack机制。因此下一次还是会从上次提交的offset继续读，就会出现重复消费。我个人觉得解决这个问题可以从两个方向来考虑：

应用端做消费幂等性处理，也即每条消息会有一个全局的key，应用端保存消费过消息的key，每次新消费一条数据，key做重复判断，若重复，则丢弃这条数据。当然这会带来额外的内存与查询开销。

同样，应用端也就是consumer端需要消息处理和offset提交这两步是事务的，也即要么操作成功要么撤回恢复之前的状态。这需要应用端有事务保障，但往往很多应用端是不支持事务的，比如kafka数据落盘hdfs，kafka数据消费完写入本地文件等等。但官方给的kafka consumer-process-kafka 给出了一个不错的参考的例子和思路。基本上遵循了分布式系统中的两阶段提交想法和思路，具体可以参见http://matt33.com/2018/11/04/kafka-transaction/

个人理解重复消费出现的概率并不会很高，在服务端改进会带来很大的性能损耗，这可能是为什么大家都选择不处理的重要原因吧。另外，本身系统与系统之间传输数据，很难做到消息的exactly once的。无论是kafka到存储系统hdfs还是spark flink下游计算系统等。若数据传输都在一个系统之内，那相对好处理一些，比如kafka的事务，保证了consume-process-producer的事务场景，也就是从kafka消费处理完毕后再到kafka，这个可以做到exactly once。





## zookeeper的作用
### zookeeper在kafka中的作用
1. **controller选举**，所有的broker在zk /controller下注册临时节点，任意一个抢先的broker注册成功，则为controller
2.  **kafka consumer负载均衡**
3.  **集群节点存活状态监测**
4.  **topic创建触发**
5.  **broker上线、下线的通知**
6. **ISR配置变更**
 


