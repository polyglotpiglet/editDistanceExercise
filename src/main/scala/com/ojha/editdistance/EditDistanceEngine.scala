package com.ojha.editdistance

import java.io.FileNotFoundException

import scala.collection.immutable.StringOps
import scala.io.Source
import scala.collection._
import scalax.collection.Graph
import scalax.collection.GraphEdge.UnDiEdge
import scalax.collection.GraphPredef._

class EditDistanceEngine {

  def computeEditDistance(start: String,
                          end: String,
                          dictionary: Set[String]): Option[Int] = {

    require(start != null && end != null, "Start and end words cannot be null")

    require(dictionary.contains(start), s"Your dictionary doesn't contain starting word $start")

    require(dictionary.contains(end), s"Your dictionary doesn't contain end word $end")

    dictionary.headOption.foreach(h => require(dictionary.forall(_.length == h.length), "Words in the dictionary are not all the same length"))

    val graph = Graph.from(dictionary, computeEdges(dictionary))
    val path = graph.get(start).shortestPathTo(graph.get(end))
    path.map(_.edges.size)
  }


  def computeEditDistance(start: String,
                          end: String,
                          filename: String): Option[Int] = {

    val resource = Option(getClass.getResource(s"/$filename"))
      .getOrElse(throw new FileNotFoundException(s"Cannot open $filename for reading - is it really there?"))

    val source = Source.fromURL(resource)
    val words = source.getLines().map(_.trim.toLowerCase).toSet
    computeEditDistance(start, end, words)
  }

  /*
  This method examines a dictionary and returns a list of all the edges.

  It performs better than the naive implementation further below.

    Example dictionary: Set("hit", "dot", "dog", "cog", "hot", "log")

    1. Firstly we compute 'subs' for each word in the dictionary"
         'hit' -> ['?it', 'h?t', 'hi?']
         'dot' -> ['?ot', 'd?t', 'do?']
         etc...

    2. Secondly we build a hashmap of these"
       '?it' -> ['hit']
       'h?t' -> ['hit', 'hot']
       'hi?' -> ['hit']
       etc....

    3. Thirdly we build the list of edges. We do this by iterating over the subs for each word and
       looking up all the matches in the hashmap. We add an edge for every unvisited node (note that we
       need only unvisited nodes because paths are undirected).

   */
  private def computeEdges(dictionary: Set[String]): Seq[UnDiEdge[String]] = {

    // step 1
    val mapOfSubs = dictionary.map { word =>
      val subs = (0 until word.length) map { case i =>
        word.patch(i, Seq('?'), 1)
      }
      word -> subs
    }.toMap

    // step2
    val cachedMap = mutable.HashMap.empty[String, Set[String]]
    dictionary.foreach { word =>
      val subs = mapOfSubs.get(word)
      subs.foreach(o => o.foreach {
        sub =>
          cachedMap(sub)
            = Set(word) ++ cachedMap.getOrElse(sub, Set.empty[String])
      })
    }

    // step 3
    val visited = mutable.Seq.empty[String]
    dictionary.flatMap { word =>
      word :+ visited
      val subs = mapOfSubs(word)
      subs.flatMap { s =>
        val links = cachedMap(s)
        links.collect {
          case l if !visited.contains(l)
            => word ~ l // ~ = undirected edge
        }
      }
    }.toSeq
  }

  // original impl for computing edges but I didn't like how slow it was
  private def computeEdgesSlowly(dictionary: Set[String]): List[UnDiEdge[String]] = {
    val (_, edges)
       = dictionary.foldLeft (dictionary.drop(1), List.empty[UnDiEdge[String]]) {
          case (acc, word) =>
            val (unvisitedWords, edgeList) = acc
            val edgesForThisWord = unvisitedWords.collect {
              case (w) if differsByOneLetter(w, word)
                 => word ~ w
            }
            (unvisitedWords.drop(1), edgeList ++ edgesForThisWord)
        }
    edges
  }

  private def differsByOneLetter(s0: String, s1: String): Boolean = {
    s0.zip(s1).count { case (a, b) => a != b } == 1
  }

}
