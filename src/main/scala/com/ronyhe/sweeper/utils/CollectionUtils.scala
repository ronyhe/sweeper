package com.ronyhe.sweeper.utils

object CollectionUtils {
  
  def itemToAmount[A](traversable: Traversable[A]): Map[A, Int] = 
    traversable.groupBy(identity).map(x => (x._1, x._2.size))
  
  def combineMaps[A, B](one: Map[A, B], two: Map[A, B]): Map[A, B]=
    one ++ tupleMap(two)
  
  def tupleMap[A, B](map: Map[A, B]): Set[(A, B)] =
    map.keySet.map(k => k -> map(k))
}
