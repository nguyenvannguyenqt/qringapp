package com.example.ringqrapp.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ringqrapp.adapter.DateSelectorAdapter
import com.example.ringqrapp.constant.CalendarUtils
import com.example.ringqrapp.databinding.FragmentSleepBinding
import com.example.ringqrapp.model.DateItem
import com.example.ringqrapp.model.SleepData
import java.util.Calendar

/**
 * Fragment hiển thị thông tin giấc ngủ của người dùng, bao gồm điểm số, thời gian ngủ,
 * và các giai đoạn giấc ngủ (ngủ sâu, ngủ nông, REM, thức).
 * Cho phép người dùng chọn ngày để xem dữ liệu giấc ngủ.
 */
class SleepFragment : Fragment() {
    // Biến binding cho layout của fragment. Sử dụng 'var' để có thể gán null trong onDestroyView.
    private var homeBinding: FragmentSleepBinding? = null
    // Getter an toàn cho binding, đảm bảo không null khi truy cập.
    private val binding get() = homeBinding!!

    // Khai báo các TextView và ImageButton để hiển thị ngày hiện tại và điều hướng.
    private lateinit var tvCurrentDate: TextView
    private lateinit var btnPreviousMonth: ImageButton
    private lateinit var btnNextMonth: ImageButton
    private lateinit var btnCalendarPicker: ImageButton

    // Khai báo RecyclerView và các TextView/ProgressBar liên quan đến dữ liệu giấc ngủ.
    private lateinit var rvDateSelector: RecyclerView
    private lateinit var tvSleepScore: TextView
    private lateinit var tvSleepTime: TextView
    private lateinit var tvFallAsleepTime: TextView
    private lateinit var tvSleepEfficiency: TextView
    private lateinit var tvSleepQuality: TextView
    private lateinit var tvDeepSleep: TextView
    private lateinit var tvLightSleep: TextView
    private lateinit var tvRemSleep: TextView
    private lateinit var tvAwake: TextView
    private lateinit var progressAwake: ProgressBar
    private lateinit var progressLightSleep: ProgressBar
    private lateinit var progressDeepSleep: ProgressBar

    // Adapter cho RecyclerView hiển thị các ngày.
    private lateinit var dateAdapter: DateSelectorAdapter
    // Lịch hiển thị ngày hiện tại trong bộ chọn ngày (tháng/năm).
    private var currentDisplayDate = Calendar.getInstance()
    // Lịch lưu trữ ngày được người dùng chọn.
    private var selectedDate = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Khởi tạo binding.
        homeBinding = FragmentSleepBinding.inflate(inflater, container, false)
        // Trả về root view từ binding.
        return binding.root
    }

    /**
     * Phương thức được gọi ngay sau khi onCreateView() trả về,
     * đảm bảo rằng view của fragment đã được tạo.
     * Khởi tạo các view, thiết lập bộ chọn ngày, lắng nghe sự kiện và tải dữ liệu.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupDateSelector()
        setupClickListeners()
        loadSleepData() // Tải dữ liệu giấc ngủ ban đầu (mặc định cho ngày hiện tại).
        updateDateDisplay() // Cập nhật hiển thị ngày trên UI.
    }


    private fun initViews() {
        tvCurrentDate = binding.tvCurrentDate
        btnPreviousMonth = binding.btnPreviousMonth
        btnNextMonth = binding.btnNextMonth
        btnCalendarPicker = binding.btnCalendarPicker
        rvDateSelector = binding.rvDateSelector
        tvSleepScore = binding.tvSleepScore
        tvSleepTime = binding.tvSleepTime
        tvFallAsleepTime = binding.tvFallAsleepTime
        tvSleepEfficiency = binding.tvSleepEfficiency
        tvSleepQuality = binding.tvSleepQuality
        tvDeepSleep = binding.tvDeepSleep
        tvLightSleep = binding.tvLightSleep
        tvRemSleep = binding.tvRemSleep
        tvAwake = binding.tvAwake
        progressAwake = binding.progressAwake
        progressLightSleep = binding.progressLightSleep
        progressDeepSleep = binding.progressDeepSleep
    }

    /**
     * Thiết lập RecyclerView để chọn ngày, bao gồm tạo danh sách ngày
     * và khởi tạo adapter.
     */
    private fun setupDateSelector() {
        // Tạo danh sách các ngày xung quanh ngày hiện tại để hiển thị trong bộ chọn.
        val dates = CalendarUtils.generateWeekDatesAroundDate(
            currentDisplayDate,
            DATE_RANGE_OFFSET
        )

        // Chọn ngày hôm nay mặc định.
        val todayPosition = CalendarUtils.getDatePosition(dates, selectedDate)
        if (todayPosition != -1) {
            dates[todayPosition].isSelected = true
        }

        // Khởi tạo DateSelectorAdapter và thiết lập listener khi một ngày được chọn.
        dateAdapter = DateSelectorAdapter(dates) { selectedDateItem ->
            selectedDate = selectedDateItem.getCalendar() // Cập nhật ngày đã chọn.
            updateDateDisplay() // Cập nhật hiển thị ngày.
            loadSleepDataForDate(selectedDateItem) // Tải dữ liệu cho ngày đã chọn.
        }

        // Cấu hình RecyclerView: thiết lập LayoutManager, adapter và cuộn đến ngày hôm nay.
        rvDateSelector.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = dateAdapter

            // Cuộn đến vị trí của ngày hôm nay để người dùng dễ nhìn.
            if (todayPosition != -1) {
                scrollToPosition(maxOf(0, todayPosition - Companion.SCROLL_OFFSET))
            }
        }
    }

    /**
     * Thiết lập các lắng nghe sự kiện click cho các nút điều hướng và bộ chọn lịch.
     */
    private fun setupClickListeners() {
        // Xử lý sự kiện click cho nút "Tháng trước".
        btnPreviousMonth.setOnClickListener {
            // Lấy ngày trong tháng hiện tại để duy trì khi chuyển tháng.
            val dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH)

            // Chuyển currentDisplayDate về tháng trước.
            currentDisplayDate.add(Calendar.MONTH, -1)
            // Thiết lập selectedDate với tháng mới và ngày cũ. Calendar sẽ tự động điều chỉnh nếu ngày không hợp lệ.
            selectedDate.set(
                currentDisplayDate.get(Calendar.YEAR),
                currentDisplayDate.get(Calendar.MONTH),
                dayOfMonth
            )

            // Nếu ngày trong tháng không hợp lệ cho tháng mới (ví dụ: ngày 31 tháng 2),
            // Calendar sẽ tự động điều chỉnh về ngày cuối cùng của tháng đó.
            // Không cần kiểm tra rõ ràng vì Calendar đã xử lý.

            updateDateSelector() // Cập nhật bộ chọn ngày.
            updateDateDisplay() // Cập nhật hiển thị ngày.
        }

        // Xử lý sự kiện click cho nút "Tháng sau".
        btnNextMonth.setOnClickListener {
            val today = Calendar.getInstance()
            // Chỉ cho phép chuyển đến tháng sau nếu tháng hiện tại nhỏ hơn tháng hiện tại thực tế
            // hoặc năm hiện tại nhỏ hơn năm hiện tại thực tế.
            if (currentDisplayDate.get(Calendar.MONTH) < today.get(Calendar.MONTH) ||
                currentDisplayDate.get(Calendar.YEAR) < today.get(Calendar.YEAR)
            ) {

                // Lấy ngày trong tháng hiện tại để duy trì khi chuyển tháng.
                val dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH)

                // Chuyển currentDisplayDate sang tháng tiếp theo.
                currentDisplayDate.add(Calendar.MONTH, 1)
                // Thiết lập selectedDate với tháng mới và ngày cũ. Calendar sẽ tự động điều chỉnh nếu ngày không hợp lệ.
                selectedDate.set(
                    currentDisplayDate.get(Calendar.YEAR),
                    currentDisplayDate.get(Calendar.MONTH),
                    dayOfMonth
                )

                updateDateSelector() // Cập nhật bộ chọn ngày.
                updateDateDisplay() // Cập nhật hiển thị ngày.
            }
        }

        // Xử lý sự kiện click cho nút chọn lịch (hiển thị DatePickerDialog).
        btnCalendarPicker.setOnClickListener {
            showDatePicker()
        }
    }

    /**
     * Hiển thị DatePickerDialog để người dùng chọn một ngày cụ thể.
     */
    private fun showDatePicker() {
        val calendar = selectedDate // Lấy ngày hiện đang được chọn để thiết lập cho DatePickerDialog.
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Khi người dùng chọn ngày, tạo một đối tượng Calendar mới.
                val newDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                    // Xóa các thành phần thời gian để so sánh chính xác theo ngày.
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                // Lấy ngày hiện tại, cũng xóa các thành phần thời gian để so sánh.
                val today = Calendar.getInstance().apply {
                    // Xóa các thành phần thời gian để so sánh chính xác theo ngày.
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                // Không cho phép chọn các ngày trong tương lai.
                if (!newDate.after(today)) { // So sánh với 'today' đã xóa thời gian.
                    selectedDate = newDate // Cập nhật ngày đã chọn.
                    currentDisplayDate = newDate.clone() as Calendar // Cập nhật ngày hiển thị.
                    updateDateSelector() // Cập nhật bộ chọn ngày.
                    updateDateDisplay() // Cập nhật hiển thị ngày.
                    loadSleepDataForDate(null) // Tải dữ liệu cho ngày mới.
                }
            },
            calendar.get(Calendar.YEAR), // Năm mặc định trong dialog.
            calendar.get(Calendar.MONTH), // Tháng mặc định trong dialog.
            calendar.get(Calendar.DAY_OF_MONTH) // Ngày mặc định trong dialog.
        )

        // Đặt ngày tối đa mà người dùng có thể chọn là ngày hiện tại,
        // bao gồm cả thời gian cuối ngày để cho phép chọn cả ngày hiện tại.
        val todayForMaxDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        datePickerDialog.datePicker.maxDate = todayForMaxDate.timeInMillis // Thiết lập ngày tối đa.
        datePickerDialog.show() // Hiển thị DatePickerDialog.
    }

    /**
     * Cập nhật danh sách ngày trong RecyclerView khi currentDisplayDate thay đổi.
     */
    private fun updateDateSelector() {
        // Tạo lại danh sách ngày dựa trên currentDisplayDate.
        val dates = CalendarUtils.generateWeekDatesAroundDate(
            currentDisplayDate,
            DATE_RANGE_OFFSET
        )

        // Tìm và chọn ngày hiện tại trong danh sách mới.
        val selectedPosition = CalendarUtils.getDatePosition(dates, selectedDate)
        if (selectedPosition != -1) {
            dates[selectedPosition].isSelected = true
        }

        dateAdapter.updateDates(dates) // Cập nhật dữ liệu cho adapter.

        // Cuộn đến ngày đã chọn trong RecyclerView.
        if (selectedPosition != -1) {
            rvDateSelector.scrollToPosition(maxOf(0, selectedPosition - Companion.SCROLL_OFFSET))
        }
    }

    /**
     * Cập nhật hiển thị ngày hiện tại trên TextView.
     */
    private fun updateDateDisplay() {
        tvCurrentDate.text = CalendarUtils.formatHeaderDate(selectedDate)
    }

    /**
     * Tải dữ liệu giấc ngủ. Hiện tại đang sử dụng dữ liệu giả.
     */
    private fun loadSleepData() {
        // Dữ liệu giấc ngủ giả lập.
        val sleepData = SleepData(
            score = 92,
            sleepTime = "7 giờ 30 phút",
            fallAsleepTime = "12 phút",
            efficiency = "--",
            quality = "--",
            deepSleep = "1 giờ 45 phút",
            lightSleep = "4 giờ 20 phút",
            remSleep = "50 phút",
            awakeTime = "35 phút"
        )

        updateUI(sleepData) // Cập nhật giao diện người dùng với dữ liệu này.
    }

    /**
     * Tải dữ liệu giấc ngủ cho một ngày được chọn.
     * TODO: Triển khai việc lấy dữ liệu thực tế từ cơ sở dữ liệu hoặc API dựa trên ngày đã chọn.
     * Hiện tại, nó chỉ tải dữ liệu giấc ngủ giả.
     */
    private fun loadSleepDataForDate(dateItem: DateItem?) {
        // TODO: Implement actual data fetching from a database or API based on the selected date.
        // For now, it loads dummy sleep data.
        loadSleepData()
    }

    /**
     * Cập nhật giao diện người dùng với dữ liệu giấc ngủ đã cung cấp.
     */
    @SuppressLint("SetTextI18n") // Bỏ qua cảnh báo về việc đặt văn bản trực tiếp.
    private fun updateUI(sleepData: SleepData) {
        tvSleepScore.text = sleepData.score.toString()
        tvSleepTime.text = sleepData.sleepTime
        tvFallAsleepTime.text = sleepData.fallAsleepTime
        tvSleepEfficiency.text = sleepData.efficiency
        tvSleepQuality.text = sleepData.quality
        tvDeepSleep.text = sleepData.deepSleep
        tvLightSleep.text = sleepData.lightSleep
        tvRemSleep.text = sleepData.remSleep
        tvAwake.text = sleepData.awakeTime

        // Cập nhật các thanh tiến trình.
        progressAwake.progress = 15
        progressLightSleep.progress = 60
        progressDeepSleep.progress = 25
    }

    /**
     * Phương thức được gọi khi view của fragment sắp bị hủy.
     * Đặt homeBinding về null để tránh rò rỉ bộ nhớ.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        homeBinding = null
    }

    /**
     * Đối tượng đồng hành (Companion Object) chứa các hằng số.
     */
    companion object {
        // Offset để tạo phạm vi ngày xung quanh ngày hiện tại (ví dụ: 8 ngày).
        private const val DATE_RANGE_OFFSET = 8
        // Offset để cuộn RecyclerView đến vị trí thích hợp (ví dụ: cuộn lùi 3 vị trí).
        private const val SCROLL_OFFSET = 3
    }
}