package edu.phystech.annnigm.main_fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import edu.phystech.annnigm.MainApplication
import edu.phystech.annnigm.R


class RealTimeFragment : Fragment() {
    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_real_time, container, false)

        val insMin: TextView = layout.findViewById(R.id.ins_min)
        val insMax: TextView = layout.findViewById(R.id.ins_max)
        val insAvg: TextView = layout.findViewById(R.id.ins_avg)

        val gluMin: TextView = layout.findViewById(R.id.glu_min)
        val gluMax: TextView = layout.findViewById(R.id.glu_max)
        val gluAvg: TextView = layout.findViewById(R.id.glu_avg)

        if (MainApplication.listInsulinMeasure.size > 0) {
            insMin.text = String.format("%.1f", MainApplication.listInsulinMeasure.min())
            insMax.text = String.format("%.1f", MainApplication.listInsulinMeasure.max())
            insAvg.text = String.format("%.1f", MainApplication.listInsulinMeasure.average())
        } else {
            insMin.text = "0"
            insMax.text = "0"
            insAvg.text = "0"
        }
        if (MainApplication.listGlusoseMeasure.size > 0) {
            gluMin.text = String.format("%.1f", MainApplication.listGlusoseMeasure.min())
            gluMax.text = String.format("%.1f", MainApplication.listGlusoseMeasure.max())
            gluAvg.text = String.format("%.1f", MainApplication.listGlusoseMeasure.average())
        } else {
            gluMin.text = "0"
            gluMax.text = "0"
            gluAvg.text = "0"
        }

        return layout
    }



}
