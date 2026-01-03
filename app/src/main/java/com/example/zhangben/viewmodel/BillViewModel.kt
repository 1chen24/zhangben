package com.example.zhangben.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.zhangben.data.AppDatabase
import com.example.zhangben.data.Bill
import kotlin.concurrent.thread

// 继承 AndroidViewModel 可以直接使用 application 上下文获取数据库
class BillViewModel(application: Application) : AndroidViewModel(application) {

    private val billDao = AppDatabase.getInstance(application).billDao()

    // 存放账单列表的观察对象
    val allBills = MutableLiveData<List<Bill>>()
    // 存放总支出的观察对象
    val totalExpense = MutableLiveData<Double>()

    /**
     * 从数据库刷新数据
     */
    fun refreshData() {
        thread {
            val list = billDao.getAllBills()
            val total = billDao.getTotalExpense()

            // 使用 postValue 在子线程更新 LiveData
            allBills.postValue(list)
            totalExpense.postValue(total ?: 0.0)
        }
    }

    /**
     * 删除账单
     */
    fun delete(bill: Bill) {
        thread {
            billDao.deleteBill(bill)
            refreshData() // 删除后刷新列表
        }
    }
}