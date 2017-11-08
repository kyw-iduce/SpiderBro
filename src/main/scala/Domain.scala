sealed trait Page
case class DeadEnd(title: String) extends Page
case class ProcessablePage(title: String) extends Page


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

  import org.jsoup._
  import com.softwaremill.sttp._

  val name = "SpiderBro"

  val robots = sttp.get(uri"$url/robots.txt").send()
  val homePage = Jsoup.connect(url).get()


}

object Main extends App {

  val crawler = new SpiderBro("http://localhost:8000")

}

