package com.toshiaki.monthlyschedule

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MonthlyPagerAdapter (private var fragments: List<Fragment>,
                          fa: FragmentActivity
) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateFragments(fragments: List<Fragment>) {
        this.fragments = fragments
        notifyDataSetChanged()
    }
}