package com.contextphoto

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.ComposeView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.contextphoto.data.Picture
import com.contextphoto.databinding.FragmentFullscreenViewBinding
import com.davemorrissey.labs.subscaleview.ImageSource
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import java.io.File


class FullscreenView(val item: Picture) : Fragment() {
    private var _binding: FragmentFullscreenViewBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFullscreenViewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (item.path.contains("VID")) {
            binding.videoView.visibility = View.VISIBLE
            binding.photoView.visibility = View.GONE
            val player = ExoPlayer.Builder(binding.videoView.context).build()
            player.playWhenReady = false
            player.setMediaItem(MediaItem.fromUri(item.uri))
            player.prepare()
            binding.videoView.player = player
//            binding.videoView.setOnClickListener { // закомментил при переносе
//                clickListener.onItemClick()
//            }
        } else {
            binding.videoView.visibility = View.GONE
            binding.photoView.visibility = View.VISIBLE
            Glide
                .with(binding.photoView.context)
                .asFile()
                .load(item.uri)
                .into(
                    object : CustomTarget<File>() {
                        override fun onResourceReady(
                            resource: File,
                            transition: Transition<in File>?,
                        ) {
                            binding.photoView.setImage(ImageSource.uri(Uri.fromFile(resource)))
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            binding.photoView.recycle()
                        }
                    },
                )
//            binding.photoView.setOnClickListener { // закомментил при переносе
//                clickListener.onItemClick()
//            }
        }
    }

}