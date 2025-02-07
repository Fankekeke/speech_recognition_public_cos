<template>
  <a-modal v-model="show" title="声纹库录入" @cancel="onClose" :width="1000" :footer="null">
    <a-radio-group v-model="featureType">
      <a-radio-button value="1">
        接线员
      </a-radio-button>
      <a-radio-button value="0">
        投诉人
      </a-radio-button>
    </a-radio-group>
    <a-spin tip="处理中..." :spinning="loading">
      <a-tabs v-model="currentTabs" default-active-key="1" style="margin-top: 15px;text-align: right" tabPosition="top">
        <a-tab-pane key="1" tab="在线录音" style="text-align: left">
          <a-alert message="录音时常需要在5~20秒内" banner />
          <a-button-group style="margin-top: 15px">
            <a-button type="primary" @click="startRecordAudio()">开始录音</a-button>
            <a-button type="primary" @click="stopRecordAudio()">停止录音</a-button>
            <a-button type="primary" @click="playRecordAudio()">播放录音</a-button>
            <a-button type="primary" @click="getWAVRecordAudioData()">获取WAV录音数据</a-button>
            <a-button type="primary" @click="downloadWAVRecordAudioData()">下载WAV录音文件</a-button>
          </a-button-group>
          <div style="margin-top: 20px;font-family: SimHei">
            <h2>录音时长：{{ recorder.duration.toFixed(2) }}</h2>
          </div>
          <div style="text-align: right;margin-top: 20px">
            <a-button key="back" @click="onClose">
              取消
            </a-button>
            <a-button key="submit" type="primary" :loading="loading" @click="handleSubmit">
              提交
            </a-button>
          </div>
        </a-tab-pane>
        <a-tab-pane key="2" tab="音源上传" force-render>
          <a-upload-dragger
            name="file"
            accept=".mp3,.mav"
            :customRequest="customRequest">
            <p class="ant-upload-drag-icon">
              <a-icon type="inbox" />
            </p>
            <p class="ant-upload-text">
              单击或将文件拖到此区域进行上传
            </p>
            <p class="ant-upload-hint">
              Support for a single or bulk upload. Strictly prohibit from uploading company data or other
              band files
            </p>
          </a-upload-dragger>
        </a-tab-pane>
        <a-tab-pane key="3" tab="音频识别测试" force-render>
          <a-upload-dragger
            name="file"
            :customRequest="customCheckRequest">
            <p class="ant-upload-drag-icon">
              <a-icon type="inbox" />
            </p>
            <p class="ant-upload-text">
              单击或将文件拖到此区域进行上传
            </p>
            <p class="ant-upload-hint">
              Support for a single or bulk upload. Strictly prohibit from uploading company data or other
              band files
            </p>
          </a-upload-dragger>
        </a-tab-pane>
      </a-tabs>
    </a-spin>
  </a-modal>
</template>

<script>
import Recorder from 'js-audio-recorder'
import {mapState} from 'vuex'
import moment from 'moment'

moment.locale('zh-cn')
const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}
export default {
  name: 'recordAdd',
  props: {
    recordAddVisiable: {
      default: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.recordAddVisiable
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      formItemLayout,
      form: this.$form.createForm(this),
      loading: false,
      currentTabs: '1',
      featureType: '1',
      recorder: new Recorder({
        sampleBits: 16, // 采样位数，支持 8 或 16，默认是16
        sampleRate: 16000, // 采样率，支持 11025、16000、22050、24000、44100、48000，根据浏览器默认值，我的chrome是48000
        numChannels: 1 // 声道，支持 1 或 2， 默认是1
      })
    }
  },
  mounted () {
  },
  methods: {
    customRequest (file) {
      const formData = new FormData()
      formData.append('file', file.file)
      formData.append('type', this.featureType)
      this.$upload('/cos/voice-feature/addFeature', formData).then((r) => {
        this.$message.success(r.data.msg)
        this.onClose()
      }).catch((r) => {
        console.error(r)
        this.$message.error(r.data.msg)
      })
    },
    customCheckRequest (file) {
      const formData = new FormData()
      formData.append('file', file.file)
      this.$upload('/cos/voice-feature/verification', formData).then((r) => {
        if (r.data && r.data.data.scoreList && r.data.data.scoreList.length > 0) {
          this.$message.success(`声纹库ID：${r.data.data.scoreList[0].featureId}, 声纹信息：${r.data.data.scoreList[0].featureInfo}`)
        } else {
          this.$message.success(`声纹库中不存在此音频特征`)
        }
      }).catch((r) => {
        console.error(r)
        this.$message.error(`${file.file.name} 文件解析失败.`)
      })
    },
    /**
     * 开始录音
     */
    startRecordAudio () {
      Recorder.getPermission().then(
        () => {
          console.log('开始录音')
          this.recorder.start()
        }, (error) => {
          this.$message.error('请先允许该网页使用麦克风')
          console.log(error)
        }
      )
    },
    /**
     * 停止录音
     */
    stopRecordAudio () {
      console.log('停止录音')
      this.recorder.stop()
    },
    /**
     * 播放录音
     */
    playRecordAudio () {
      console.log('播放录音')
      this.recorder.play()
    },
    /**
     * 获取PCB录音数据
     */
    getPCBRecordAudioData () {
      var pcmBlob = this.recorder.getPCMBlob()
      console.log(pcmBlob)
    },
    /**
     * 获取WAV录音数据
     */
    getWAVRecordAudioData () {
      var wavBlob = this.recorder.getWAVBlob()
      console.log(wavBlob)
    },
    /**
     * 下载PCB录音文件
     */
    downloadPCBRecordAudioData () {
      this.recorder.downloadPCM('badao')
    },
    /**
     * 下载WAV录音文件
     */
    downloadWAVRecordAudioData () {
      this.recorder.downloadWAV('badao')
    },
    handleChange (info) {
      const status = info.file.status
      if (status === 'uploading') {
        this.loading = true
      }
      if (status !== 'uploading') {
        console.log(info.file, info.fileList)
        let fileInfo = info.file
        console.log('fileInfo.response', fileInfo.response)
        if (fileInfo && fileInfo.response.data.scoreList) {
          this.$message.success(`声纹库ID：${fileInfo.response.data.scoreList[0].featureId}, 声纹信息：${fileInfo.response.data.scoreList[0].featureInfo}`)
        }
      }
      if (status === 'done') {
        this.loading = false
        if (this.currentTabs.toString() !== '3') {
          this.$message.success(`${info.file.name} 文件上传成功,正在解析中...`)
          this.onClose()
        }
      } else if (status === 'error') {
        this.$message.error(`${info.file.name} 文件上传失败.`)
        this.loading = false
      }
    },
    onClose () {
      this.$emit('close')
    },
    handleSubmit () {
      let wavBlob = this.recorder.getWAVBlob()
      console.log(wavBlob)
      if (wavBlob.size < 150000 || wavBlob.size > 680000) {
        this.$message.error('录音时常需要在5~20秒内.')
        return false
      }
      this.loading = true
      const formData = new FormData()
      formData.append('file', wavBlob)
      formData.append('type', this.featureType)
      this.$upload('/test/igrRecogntion/file', formData).then((r) => {
        this.$emit('success')
      }).catch(() => {
        this.loading = false
      })
    }
  }
}
</script>
<style scoped>

</style>
