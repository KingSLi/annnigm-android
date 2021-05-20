package edu.phystech.annnigm.cards

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import edu.phystech.annnigm.MeasureType
import edu.phystech.annnigm.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CardsAdapter(cardsList: ArrayList<Card>) : RecyclerView.Adapter<CardsAdapter.CardViewHolder>() {

    private var cards: ArrayList<Card> = cardsList
    private var mListener: OnItemClickListener? = null

    public interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_item_view, parent, false)
        return CardViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val (type, description, time) = cards[position]
        holder.type.setImageResource(when(type) {
            MeasureType.MEAL -> R.drawable.ic_eat_type
            MeasureType.INSULIN -> R.drawable.ic_insulin
            MeasureType.GLUCOSE -> R.drawable.ic_device
            else -> R.drawable.ic_add_circle_outline_black_24dp
        })
        holder.description.text = description
        holder.typeText.text = type.toString()
        holder.time.text = transformDateTime(time)  // "hh:mm dd.MM.yyyy"
    }

    fun transformDateTime(time: String) : String {
        val date = if ("+" in time)
            LocalDateTime.parse(time.slice(IntRange(0, time.length - 6)))
        else
            LocalDateTime.parse(time)
        val formatter = DateTimeFormatter.ofPattern("hh:mm dd.MM.yyyy")
        return date.format(formatter)
    }


    override fun getItemCount(): Int {
        return cards.size
    }

    fun addCards(items: List<Card>) {
        cards.addAll(items)
    }

    fun replaceCards(newListCards: ArrayList<Card>) {
        cards = newListCards
    }

    fun getCard(position: Int) = cards[position]

    class CardViewHolder(itemView: View,
                         private val clickListener : OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        val type: ImageView = itemView.findViewById(R.id.measure_type)
        var time: TextView = itemView.findViewById(R.id.card_time) as TextView
        val typeText: TextView = itemView.findViewById(R.id.text_measure_type) as TextView
        var description: TextView = itemView.findViewById(R.id.card_description) as TextView
        var buttonDelete: ImageButton = itemView.findViewById(R.id.card_remove_button)

        init {
            buttonDelete.setOnClickListener{
                if (clickListener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onItemClick(position)
                    }
                }
            }
        }
    }

}

