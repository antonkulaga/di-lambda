package org.openscience.school.lambda.views
import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.{ItemsSeqView, CollectionView, BindableView, ItemsSetView}
import org.opensciencce.school.lambda.domain._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement
import rx._
import rx.core.Var
import rx.ops._
import scala.collection.immutable._
import org.denigma.binding.extensions._

class Measurements(val elem:HTMLElement,val items: Var[SortedSet[Var[Measurement]]],blanks:Var[Seq[Double]])

  extends BindableView  with ItemsSetView {

  override type Item = Var[Measurement]

  override type ItemView = MeasurementView


 // lazy val items: Var[SortedSet[Var[Measurement]]] = Var(SortedSet(ms:_*))

  override def newItem(item: Var[Measurement]): MeasurementView = this.constructItemView(item){
    case (el,args)=>
      println(item.now)
      new MeasurementView(el,item,blanks).withBinder(m=>new GeneralBinder(m))
  }


  items.handler{
    println("*************"+items.now)
  }
  /*



  override protected def subscribeUpdates() = {
    template.style.display = "none"
    this.items.now.foreach(i=>this.addItemView(i,this.newItem(i))) //initialization of views
    updates.onChange("ItemsUpdates")(upd=>{
      upd.added.foreach(onInsert)
      upd.removed.foreach(onRemove)
    })
  }
*/


}


class SampleOptionView(val elem:HTMLElement,sample:Var[Sample]) extends BindableView{
  val value = sample.map(_.name)
}
class SuggestView(val elem:HTMLElement,val items:Var[Seq[Var[Sample]]]) extends ItemsSeqView{
  type Item = Var[Sample]
  override type ItemView = SampleOptionView

  override def newItem(item: Item): ItemView = constructItemView(item){
    case (el,mp)=>new SampleOptionView(el,item).withBinder(new GeneralBinder(_))
  }
}

class MeasurementView(val elem:HTMLElement,measurement:Var[Measurement],blanks:Var[Seq[Double]])
  extends BindableView
{
  import org.denigma.binding.extensions._
  val sample = measurement.map(m=>m.sample.name)
  val channel = measurement.map(m=>m.channel.toString)
  val value = measurement.map(m=>m.value.toString)
  val blank = measurement.map(m=>m.blank.toString)
  val absorbance = measurement.map(m=>m.absorbance.toString)
  val transmittance = measurement.map(m=>m.transmittance.toString)
  val updateBlank: Var[MouseEvent] = Var(Events.createMouseEvent())
  updateBlank.handler{
    measurement() = measurement.now.copy(blank = blanks.now(measurement.now.channel))
  }



  /*val datetime = measurement.map(m=>m.date.getTime.toString)
  val diode = measurement.map(m=>m.diode)
  val value = measurement.map(m=>m.value.toString)*/
}
