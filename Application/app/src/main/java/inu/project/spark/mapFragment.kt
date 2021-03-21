package inu.project.spark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.daum.mf.map.api.MapView


class mapFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mapView = MapView(activity)
        val mapViewContainer = requireView().findViewById<View>(R.id.map_view) as ViewGroup
        mapViewContainer.addView(mapView)
    }
}