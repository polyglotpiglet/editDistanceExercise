package com.ojha.editdistance

import java.io.FileNotFoundException

import com.ojha.{UnitTest, IntegrationTest}
import org.scalatest.{Matchers, FlatSpec}

class EditDistanceEngineSpec extends FlatSpec with Matchers {

  it should "work for test input" taggedAs UnitTest in {
    val sut = new EditDistanceEngine()
    val words = Set("hit", "dot", "dog", "cog", "hot", "log")
    val length = sut.computeEditDistance("hit", "cog", words)
    length should equal(Some(4))
  }

  it should "return None if no mapping is found" taggedAs UnitTest in {
    val sut = new EditDistanceEngine()
    val words = Set("hit", "dot", "dog", "cog", "hot", "log", "raw")
    val length = sut.computeEditDistance("hit", "raw", words)
    length should equal(None)
  }

  it should "throw IllegalArgumentException if trying to find a path where start word isn't in the dictionary" taggedAs UnitTest in {
    val sut = new EditDistanceEngine()
    val thrown = intercept[IllegalArgumentException] {
      sut.computeEditDistance("cat", "dog", Set("raw", "pip", "dog"))
    }
    thrown.getMessage should equal("requirement failed: Your dictionary doesn't contain starting word cat")
  }

  it should "throw IllegalArgumentException if trying to find a path where end word isn't in the dictionary" taggedAs UnitTest in {
    val sut = new EditDistanceEngine()
    val thrown = intercept[IllegalArgumentException] {
      sut.computeEditDistance("cat", "dog", Set("raw", "pip", "cat"))
    }
    thrown.getMessage should equal("requirement failed: Your dictionary doesn't contain end word dog")
  }

  it should "throw IllegalArgumentException if dictionary words are different lengths" taggedAs UnitTest in {
    val sut = new EditDistanceEngine()
    val thrown = intercept[IllegalArgumentException] {
      sut.computeEditDistance("cat", "dog", Set("raw", "pip", "cat", "dog", "puppy"))
    }
    thrown.getMessage should equal("requirement failed: Words in the dictionary are not all the same length")
  }

  it should "throw IllegalArgumentException if start is null" taggedAs UnitTest in {
    val sut = new EditDistanceEngine()
    val thrown = intercept[IllegalArgumentException] {
      sut.computeEditDistance(null, "dog", Set("raw", "pip", "cat", "dog"))
    }
    thrown.getMessage should equal("requirement failed: Start and end words cannot be null")
  }

  it should "throw IllegalArgumentException if end is null" taggedAs UnitTest in {
    val sut = new EditDistanceEngine()
    val thrown = intercept[IllegalArgumentException] {
      sut.computeEditDistance("cat", null, Set("raw", "pip", "cat", "dog"))
    }
    thrown.getMessage should equal("requirement failed: Start and end words cannot be null")
  }

  it should "calculate length for four letter words" taggedAs IntegrationTest in {
    val sut = new EditDistanceEngine()
    val l = sut.computeEditDistance("belt", "must", "wordsByLength/fours.txt") // belt best bust must
    l should equal(Some(3))
  }

  it should "throw exception when file doesnt exist" taggedAs UnitTest in {
    val sut = new EditDistanceEngine()
    val thrown = intercept[FileNotFoundException] {
      sut.computeEditDistance("cat", "dog", "wordsByLength/404.txt")
    }
    thrown.getMessage should equal(s"Cannot open wordsByLength/404.txt for reading - is it really there?")
  }
}
