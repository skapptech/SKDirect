package com.skdirect.litho

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.litho.ComponentContext
import com.facebook.litho.LithoView
import com.facebook.litho.widget.Text
import com.skdirect.R
import com.skdirect.databinding.ActivityLithoBinding

class LithoActivity : AppCompatActivity() {
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(GamesListViewModel::class.java)
    }

    private val gamesListController = GamesListController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityLithoBinding>(this, R.layout.activity_litho)
        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.adapter = gamesListController.adapter

        viewModel.gamesLiveData.observe(this, Observer { container ->
            gamesListController.setData(container)
        })
    }
}