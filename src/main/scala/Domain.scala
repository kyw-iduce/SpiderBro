import org.jsoup.nodes.Document

import scala.collection.JavaConversions._
import scala.collection._


sealed trait Page
case class DeadEnd(title: String) extends Page
case class ProcessablePage(title: String) extends Page

sealed case class Link(title: String, href: String)

object Page {
  def fromTitle(title: String): Page =
    title match {
      case deadSpider if title.startsWith("Oh no!") => DeadEnd(deadSpider)
      case _ => ProcessablePage(title)
    }
}
object SpiderBro {
  def apply(url: String): SpiderBro = new SpiderBro(url)
}
class SpiderBro(url: String) {
  import com.softwaremill.sttp._
  import org.jsoup._
  val name = "SpiderBro"
  implicit val backend = HttpURLConnectionBackend()

  val robots: Id[Response[String]] = sttp.get(uri"$url/robots.txt").send()

  /**
    * Disallowed links from robot.txt
    */
  val disallowed: List[Link] = robots.body.getOrElse("bad")
    .split("\n").map(_.trim).iterator.toList.map(l => l.split(":"))
    .filter { l => l(0).startsWith("Disallow") }
    .map { l =>
      Link(l(0), l(1))
    }


  /**
    * Fetch the html code
    */
  val homePage: Document = Jsoup.connect(url).get()

  /**
    * Extracts links from a document
    *
    */
  def getLinksPage(doc: Document): Seq[Link] = {
    val links = doc.select("a[href]").iterator.toList
    links.map { l => Link(l.text, l.attr("href")) }
  }



  def url(uri: String): String = s"http://localhost:8000/$uri"

  /**
    * All allowed links.
    */
  val links = getLinksPage(homePage)
    .filter(l => Page.fromTitle(l.title).isInstanceOf[ProcessablePage])
    .filter(isDisallowed(disallowed))
    .flatMap(l => Seq(l) ++ getLinksPage(Jsoup.connect(url(l.href)).get()))
    .foreach {
      case Link(title, link) => println(s"http://localhost:8000/$link")
    }

  def isDisallowed(disallowed:Seq[Link])(link: Link): Boolean ={
    val l = link.href.trim
    val d = disallowed.map(_.href.trim.drop(1))
    !d.contains(l)
  }
}
object Main extends App {
  val crawler = new SpiderBro("http://localhost:8000/")
}