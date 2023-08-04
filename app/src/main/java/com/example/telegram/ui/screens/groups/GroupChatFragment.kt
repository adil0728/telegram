package com.example.telegram.ui.screens.groups

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.AbsListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.databinding.FragmentSingleChatBinding
import com.example.telegram.model.CommonModel
import com.example.telegram.model.UserModel
import com.example.telegram.ui.screens.base.BaseFragment
import com.example.telegram.ui.message_recycler_view.views.AppViewFactory
import com.example.telegram.ui.screens.main_list.MainListFragment
import com.example.telegram.utilits.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GroupChatFragment(private val group: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {

    private lateinit var binding: FragmentSingleChatBinding
    private lateinit var mListenerToolbarInfo: AppValueEventListener
    private lateinit var mReceivingUser: UserModel
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: GroupChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListeners: AppChildEventListener
    private var mCountMessages = 15
    private var mIsScrolling = false
    private var mSmoothScrollToPosition = true
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAppVoiceRecorder: AppVoiceRecorder
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>

    private lateinit var mToolbarInfo: ConstraintLayout
    private lateinit var toolbarImage: CircleImageView
    private lateinit var contactFullname: TextView
    private lateinit var contactStatus: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSingleChatBinding.inflate(inflater, container, false)
        initFields()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initToolbar()
        initRecyclerView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        mBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetChoice)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mToolbarInfo = APP_ACTIVITY.mConstrainToolbar
        toolbarImage = APP_ACTIVITY.toolbarImage
        contactFullname = APP_ACTIVITY.contactFullname
        contactStatus = APP_ACTIVITY.contactStatus
        mSwipeRefreshLayout = binding.chatSwipeRefresh
        mLayoutManager = LinearLayoutManager(this.context)
        mAppVoiceRecorder = AppVoiceRecorder()

        val chatInputMessage = binding.chatInputMessage
        val chatBtnSendMessage = binding.chatBtnSendMessage
        val chatBtnSendAttach = binding.chatBtnSendAttach
        val chatBtnVoice = binding.chatBtnVoice

        setHasOptionsMenu(true)

        chatInputMessage.addTextChangedListener(AppTextWatcher {
            val string = chatInputMessage.text.toString()
            if (string.isEmpty() || string == "Запись") {
                chatBtnSendMessage.visibility = View.GONE
                chatBtnSendAttach.visibility = View.VISIBLE
                chatBtnVoice.visibility = View.VISIBLE
            } else {
                chatBtnSendMessage.visibility = View.VISIBLE
                chatBtnSendAttach.visibility = View.GONE
                chatBtnVoice.visibility = View.GONE

            }
        })

        chatBtnSendAttach.setOnClickListener { attach() }

        CoroutineScope(Dispatchers.IO).launch {
            chatBtnVoice.setOnTouchListener { v, event ->
                if (checkPermission(RECORD_AUDIO)) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        //record
                        chatInputMessage.setText("Запись")
                        chatBtnVoice.setColorFilter(
                            ContextCompat.getColor(
                                APP_ACTIVITY,
                                com.mikepenz.materialize.R.color.colorPrimary
                            )
                        )
                        val messageKey = getMessageKey(group.id)
                        mAppVoiceRecorder.startRecord(messageKey)
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        //stop record
                        chatInputMessage.setText("")
                        chatBtnVoice.colorFilter = null
                        mAppVoiceRecorder.stopRecord { file, messageKey ->

                            uploadFileToStorage(
                                Uri.fromFile(file),
                                messageKey,
                                group.id,
                                TYPE_MESSAGE_VOICE
                            )
                            mSmoothScrollToPosition = true
                        }

                    }
                }
                true
            }

        }

    }

    private fun attach() {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.btnAttachFile.setOnClickListener { attachFile() }
        binding.btnAttachImage.setOnClickListener { attachImage() }


    }

    private fun attachFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


    }

    private fun attachImage() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(300, 300)
            .start(APP_ACTIVITY, this)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

    }

    private fun initRecyclerView() {
        mRecyclerView = binding.chatRecyclerView
        mAdapter = GroupChatAdapter()

        mRefMessages = REF_DATABASE_ROOT.child(NODE_GROUPS).child(group.id).child(NODE_MESSAGES)

        mRecyclerView.adapter = mAdapter
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = mLayoutManager

        mMessagesListeners = AppChildEventListener {
            val message = it.getCommonModel()

            if (mSmoothScrollToPosition) {
                mAdapter.addItemToBottom(AppViewFactory.getView(message)) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)

                }
            } else {
                mAdapter.addItemToTop(AppViewFactory.getView(message)) {
                    mSwipeRefreshLayout.isRefreshing = false

                }
            }
        }

        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListeners)
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (mIsScrolling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }
        })

        mSwipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        mSmoothScrollToPosition = false
        mIsScrolling = false
        mCountMessages += 10
        mRefMessages.removeEventListener(mMessagesListeners)
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListeners)
    }

    private fun initToolbar() {
        mToolbarInfo.visibility = View.VISIBLE
        mListenerToolbarInfo = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar()
        }
        mRefUser = REF_DATABASE_ROOT.child(NODE_USERS).child(group.id)
        mRefUser.addValueEventListener(mListenerToolbarInfo)

        binding.chatBtnSendMessage.setOnClickListener {
            mSmoothScrollToPosition = true
            val message = binding.chatInputMessage.text.toString()
            if (message.isEmpty()) {
                showToast("Введите сообщение")
            } else sendMessageToGroup(message, group.id, TYPE_TEXT) {
                binding.chatInputMessage.setText("")
            }
        }
    }

    private fun initInfoToolbar() {
        if (mReceivingUser.fullname.isEmpty()) {
            contactFullname.text = group.fullname
        } else contactFullname.text = mReceivingUser.fullname

        toolbarImage.downloadAndSetImage(mReceivingUser.photoUrl)
        contactStatus.text = mReceivingUser.state
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null ) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val uri = CropImage.getActivityResult(data).uri
                    val messageKey = getMessageKey(group.id)
                    uploadFileToStorage(uri, messageKey, group.id, TYPE_MESSAGE_IMAGE)
                    mSmoothScrollToPosition = true
                }

                PICK_FILE_REQUEST_CODE -> {
                    val uri = data.data
                    val messageKey = getMessageKey(group.id)
                    val filename = getFilenameFromUri(uri!!)
                    uploadFileToStorage(uri, messageKey, group.id, TYPE_MESSAGE_FILE,filename)
                    mSmoothScrollToPosition = true
                }
            }

        }
    }

    override fun onPause() {
        super.onPause()
        mToolbarInfo.visibility = View.GONE
        mRefUser.removeEventListener(mListenerToolbarInfo)
        mRefMessages.removeEventListener(mMessagesListeners)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAppVoiceRecorder.releaseRecord()
        mAdapter.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.single_chat_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear_chat -> clearChat(group.id){
                showToast("Чат очищен")
                replaceFragment(MainListFragment())

            }

            R.id.menu_delete_chat -> deleteChat(group.id){
                showToast("Чат удален")
                replaceFragment(MainListFragment())
            }
        }
        return true
    }

}
