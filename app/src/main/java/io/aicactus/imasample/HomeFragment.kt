package io.aicactus.imasample

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.aicactus.imasample.databinding.FragmentHomeBinding

class HomeFragment: Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val homeViewModel: HomeViewModel by activityViewModels()

    private val playerFragments: Array<String> = arrayOf(
        "Aicactus Player",
        "ExoPlayer",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listBannerTypes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = HomeListAdapter(playerFragments)
        }

        binding.btnTryAgain.setOnClickListener {
            homeViewModel.initAdsNetworkSDK()
        }

        homeViewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.indicator.visibility = View.VISIBLE
                binding.errorPanel.visibility = View.GONE
                binding.listBannerTypes.visibility = View.GONE
            } else {
                binding.indicator.visibility = View.GONE

                homeViewModel.errorMessage.value?.let {
                    binding.errorPanel.visibility = View.VISIBLE
                    binding.lblMessageError.text = it
                    binding.listBannerTypes.visibility = View.GONE
                } ?: run {
                    binding.errorPanel.visibility = View.GONE
                    binding.listBannerTypes.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeViewModel.initAdsNetworkSDK()
    }

    class HomeListAdapter(private val dataSet: Array<String>): RecyclerView.Adapter<HomeListAdapter.ViewHolder>() {

        class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val textTitle: AppCompatTextView = view.findViewById(R.id.text_title)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_ad_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textTitle.text = dataSet[position]
            holder.itemView.setOnClickListener{ view ->
                val navController = view.findNavController()
                when (position) {
                    0 -> navController.navigate(R.id.action_homeFragment_to_aicactusPlayerFragment)
                    1 -> navController.navigate(R.id.action_homeFragment_to_exoPlayerFragment)
                }
            }
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }
    }
}