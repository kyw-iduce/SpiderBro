sealed trait Page
case class WrongTurn(title: String) extends Page

object Page{
  def fromTitle(title: String): Page =
    title match {
      case deadSpider if title.startsWith("Oh no!") => WrongTurn(deadSpider)
    }
}