//package ch.epfl.insynth.reconstruction
//
//import insynth.reconstruction.stream._
//import insynth.streams._
//import insynth.streams.ordered._
//
//import insynth.util.logging.HasLogger
//
//class OrderedStreamFactory extends StreamFactory[Node] with HasLogger {
//  
//  override def makeEmptyStreamable = Empty
//
//  override def makeSingleton[U <: Node](element: U) = Singleton(element)
//  
//  override def makeSingletonList[U <: Node](element: List[U]) = Singleton(element)
//  
////  override def makeSingleStream[U <: T](stream: => Stream[U], isInfiniteFlag: Boolean) =
////    SingleStream(stream, isInfiniteFlag)
//  
//  override def makeUnaryStream[X, Y <: Node](streamable: Streamable[X], modify: X => Y, modifyVal: Option[Int => Int] = None) =
//    UnaryStream(streamable.asInstanceOf[OrderedStreamable[X]], modify, modifyVal)
//  
//  override def makeUnaryStreamList[X, Y <: Node](streamable: Streamable[X], modify: X => List[Y]) =
//    UnaryStream(streamable.asInstanceOf[OrderedStreamable[X]], modify)
//  
//  override def makeBinaryStream[X, Y, Z <: Node](s1: Streamable[X], s2: Streamable[Y])(combine: (X, Y) => List[Z]) =
//    BinaryStream(s1.asInstanceOf[OrderedStreamable[X]], s2.asInstanceOf[OrderedStreamable[Y]])(combine)
//  
//  override def makeRoundRobbin[U <: Node](streams: Seq[Streamable[U]]) =
//    RoundRobbin(streams.asInstanceOf[Seq[OrderedStreamable[Node]]])
//  
//  override def makeLazyRoundRobbin[U <: Node](initStreams: List[Streamable[U]]) =
//    LazyRoundRobbin[Node](initStreams.asInstanceOf[List[OrderedStreamable[Node]]])
//      
//  def getFinalStream(streamable: Streamable[Node]) = 
//    streamable match {
//      case os: OrderedStreamable[_] =>
//        fine("returning ordered streamable")
//        os.getStream zip os.getValues.map(_.toFloat)
//      case _: Streamable[_] =>
//        fine("returning unordered streamable")
//        streamable.getStream zip Stream.continually(0f)
//    }
//}