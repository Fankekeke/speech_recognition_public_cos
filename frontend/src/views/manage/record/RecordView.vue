<template>
  <a-drawer
    placement="right"
    width="100%"
    :closable="false"
    :visible="show"
    @close="onClose"
    wrapClassName="aa"
    :getContainer="false">
    <div style="width: 100%;font-size: 13px;font-family: SimHei" v-if="recordData !== null">
      <a-row style="height:100vh;">
        <a-col :span="2" style="height: 100%;background-color: #FFFFFF">
          <div style="padding: 35px 15px 15px 15px">
            <a-button type="primary" style="width: 100%" @click="onClose"> <a-icon type="left" />返回</a-button>
            <div style="margin-top: 25px;padding: 10px">
              <div style="text-align: center;margin-top: 25px" @click="currentTab = 1">
                <a-avatar shape="square" :size="35" src="/static/img/home.png" />
                <p style="font-size: 13px;margin-top: 8px">语音解析</p>
              </div>
              <!-- <div style="text-align: center;margin-top: 45px" @click="currentTab = 2">
                <a-avatar shape="square" :size="35" src="/static/img/download.png" />
                <p style="font-size: 13px;margin-top: 8px">数据下载</p>
              </div> -->
              <div style="text-align: center;margin-top: 45px" @click="currentTab = 3">
                <a-avatar shape="square" :size="35" src="/static/img/record.png" />
                <p style="font-size: 13px;margin-top: 8px">用户画像</p>
              </div>
              <div style="text-align: center;margin-top: 45px" @click="currentTab = 4">
                <a-avatar shape="square" :size="35" src="/static/img/template.png" />
                <p style="font-size: 13px;margin-top: 8px">处理方案</p>
              </div>
              <div style="text-align: center;margin-top: 45px" @click="currentTab = 5">
                <a-avatar shape="square" :size="35" src="/static/img/delete.png" />
                <p style="font-size: 13px;margin-top: 8px;color: #ff3b30">数据删除</p>
              </div>
            </div>
          </div>
        </a-col>
        <a-col :span="22" style="height: 100%;overflow-y: auto">
          <div style="padding: 35px 15px 15px 15px">
            <a-row style="padding-left: 24px;padding-right: 24px;">
              <a-col style="margin-bottom: 15px">
                <span style="font-size: 17px;font-weight: 650;color: #333">{{ recordData.fileName ? recordData.fileName : '- -' }}</span>
                <br/>
                <span style="font-size: 13px;color: #999">{{ recordData.code ? recordData.code : '- -' }}</span>
              </a-col>
              <a-col :span="24" style="margin: 0 auto" v-if="currentTab == 1 && recordData.status == 0">
                <div style="font-family: SimHei;background-color: #FFFFFF;padding: 150px 30px 150px 30px;text-align: center">
                  <a-avatar shape="square" :size="70" src="/static/img/loading.png" />
                  <p style="font-size: 20px;font-weight: bold;margin-top: 25px;margin-bottom: 45px">语音数据正在处理中，请稍后再进行查看</p>
                  <a-button type="primary" @click="onClose"> <a-icon type="left" />返回</a-button>
                </div>
              </a-col>
              <a-col :span="24" style="margin: 0 auto" v-if="currentTab == 1 && recordData.status == 2">
                <div style="font-family: SimHei;background-color: #FFFFFF;padding: 150px 30px 150px 30px;text-align: center">
                  <a-avatar shape="square" :size="70" src="/static/img/error.png" />
                  <p style="font-size: 20px;font-weight: bold;margin-top: 25px;margin-bottom: 45px">解析发生错误，请手动重新进行解析</p>
                  <a-button type="primary" @click="onClose"> <a-icon type="reload" />重新解析</a-button>
                </div>
              </a-col>
              <a-col :span="24" style="margin: 0 auto" v-if="currentTab == 2">
                <div style="font-family: SimHei;background-color: #FFFFFF;padding: 150px 30px 150px 30px;text-align: center">
                  <img alt="example" style="width: 150px" src="/static/img/data.png" />
                  <p style="font-size: 20px;font-weight: bold;margin-top: 25px;margin-bottom: 45px">下载此条数据音频</p>
                  <a-button type="primary" @click="onDownload"> <a-icon type="download"/>下载</a-button>
                </div>
              </a-col>
              <a-col :span="24" style="margin: 0 auto" v-if="currentTab == 3">
                <div style="font-family: SimHei;">
                  <a-row>
                    <a-col :span="6">
                      <img alt="example" style="width: 100px" src="/static/img/total.png" />
                      <br/>
                      <a-col style="margin-bottom: 15px;margin-top: 25px">
                        <span style="font-size: 15px;font-weight: 650;color: #333">投诉人信息识别 </span>
                      </a-col>
                      <a-col>
                        <div style="background-color: #FFFFFF;padding: 15px;width: 85%">
                          <div style="font-size: 14px;line-height: 30px">声纹标识：{{ voiceFeature ? voiceFeature.code : '- -' }}</div>
                          <br/>
                          <div style="font-size: 14px;line-height: 30px">联系电话：{{ recordData.phone ? recordData.phone : '- -' }}</div>
                          <br/>
                          <div style="font-size: 14px;line-height: 30px" v-if="voiceFeature != null">性别：
                            <a-tag color="blue" v-if="voiceFeature.sex == 0">女性</a-tag>
                            <a-tag color="blue" v-if="voiceFeature.sex == 1">男性</a-tag>
                          </div>
                          <br/>
                          <div style="font-size: 14px;line-height: 30px" v-if="voiceFeature != null">年龄：
                            <span v-if="voiceFeature.ageRate == 0">12~40岁</span>
                            <span v-if="voiceFeature.ageRate == 1">0~12岁</span>
                            <span v-if="voiceFeature.ageRate == 2">40岁以上</span>
                          </div>
                          <br/>
                          <div style="font-size: 14px;line-height: 30px" v-if="complaintMoods != null">情绪：
                            <a-tag color="orange" v-for="(item, index) in complaintMoods" :key="index">{{item}}</a-tag>
                          </div>
                        </div>
                      </a-col>
                    </a-col>
                    <a-col :span="18">
                      <apexchart type="bar" height="280" :options="chartOptions" :series="series"></apexchart>
                    </a-col>
                  </a-row>
                  <a-row>
                    <a-col style="margin-bottom: 15px;margin-top: 25px">
                      <span style="font-size: 15px;font-weight: 650;color: #333">投诉记录 </span>
                    </a-col>
                    <a-timeline>
                      <a-timeline-item v-for="(item, index) in recordList" :key="index">
                        <span>{{ item.createDate }} {{ item.paragraphTitle }}</span>
                      </a-timeline-item>
                    </a-timeline>
                  </a-row>
                </div>
              </a-col>
              <a-col :span="24" style="margin: 0 auto" v-if="currentTab == 4">
                <div style="font-family: SimHei;">
                  <a-row :gutter="25">
                    <a-col :span="16">
                      <div style="height: 90vh;overflow-y: auto;">
                        <a-spin :spinning="llmLoading" :delay="500" tip="生成中。。。">
                          <div v-if="callRecord.length > 0">
                            <a-comment v-for="(item, index) in callRecord" :key="index">
                              <a slot="author" style="font-size: 14px">AI小助理</a>
                              <a slot="datetime" style="font-size: 14px;">生成于 {{ item.responseTime }}</a>
                              <a-avatar
                                size="50"
                                shape="square"
                                slot="avatar"
                                style="backgroundColor:#1677ff" icon="smile"/>
                              <div slot="content">
                                <a-card style="margin-top: 25px;padding: 25px" >
                                  <p style="font-family: SimHei;line-height: 30px" v-html="item.outputText"></p>
                                </a-card>
                              </div>
                            </a-comment>
                          </div>
                          <div style="font-family: SimHei;background-color: #FFFFFF;padding: 150px 30px 150px 30px;text-align: center" v-else>
                              <img alt="example" style="width: 150px" src="/static/img/Search.png" />
                              <p style="font-size: 20px;font-weight: bold;margin-top: 25px;margin-bottom: 45px">未生成AI处理信息</p>
                          </div>
                        </a-spin>
                      </div>
                    </a-col>
                    <a-col :span="8">
                      <div style="text-align: center">
                        <img
                          height="250"
                          alt="example"
                          src="/static/img/chat.png"/>
                        <a-card hoverable>
                          <a-card-meta>
                            <div slot="title" style="padding: 20px 10px 10px 10px;text-align: left;font-size: 15px">
                              <a-icon type="message" theme="twoTone" /> 智能问答
                            </div>
                          </a-card-meta>
                          <div style="padding: 10px;margin-bottom: 10px" v-if="callRecord.length > 0">
                            <a-card-meta style="margin-top: 10px; min-height: 100px;" v-if="callRecordContuineTip.length > 0">
                              <div slot="title" style="padding: 20px 10px 10px 10px;text-align: left;font-size: 15px">
                                <a-icon type="edit" theme="twoTone" /> 快捷操作
                              </div>
                              <div slot="description">
                                <a-tag color="#55acee" v-for="(item, index) in callRecordContuineTip" :key="index" @click="aiIssue = item">
                                  {{item}}
                                </a-tag>
                              </div>
                            </a-card-meta>
                            <a-textarea
                              v-model="aiIssue"
                              placeholder="填写需要处理的问题"
                              :autosize="{ minRows: 5, maxRows: 5 }"/>
                            <div style="text-align: right;margin-bottom: 20px;margin-right: 8px; margin-top: 20px;">
                              <a-button type="primary" @click="continueCallLLM"> 发 送<a-icon type="right"/> </a-button>
                            </div>
                          </div>
                          <div style="padding: 10px;margin-bottom: 10px" v-else>
                            <div>
                              <span>回复模板：</span>
                              <a-select
                                v-model="replyTemplateId"
                                show-search
                                placeholder="输入模板名称或ID"
                                style="width: 200px"
                                :default-active-first-option="false"
                                :show-arrow="true"
                                :filter-option="false"
                                :options="replyTemplateList"
                                @search="handleReplyTemplateSearch"
                                @change="handleReplyTemplateChange">
                                <template slot="notFoundContent">
                                  <div style="display: flex;justify-content: space-around; align-items: center;">
                                    <span>没有找到模板</span><a href="#" @click="toTemplateManage">去创建</a>
                                  </div>
                                </template>
                              </a-select>
                            </div>
                            <div style="margin-top: 20px;">
                              <a-button type="primary" @click="callLLM">开始生成<a-icon type="right"/> </a-button>
                            </div>
                          </div>
                        </a-card>
                      </div>
                    </a-col>
                  </a-row>
                </div>
              </a-col>
              <a-col :span="24" style="margin: 0 auto" v-if="currentTab == 5">
                <div style="font-family: SimHei;background-color: #FFFFFF;padding: 150px 30px 150px 30px;text-align: center">
                  <a-avatar shape="square" :size="70" src="/static/img/error.png" />
                  <p style="font-size: 20px;font-weight: bold;margin-top: 25px;margin-bottom: 45px">确定要删除此条解析数据吗？</p>
                  <a-button type="danger" @click="onClose"> <a-icon type="rest" />删除</a-button>
                </div>
              </a-col>
              <a-col :span="24" v-if="currentTab == 1 && recordData.status == 1">
                <a-row>
                  <img alt="example" style="width: 100px" src="/static/img/total.png" />
                  <a-col style="margin-bottom: 15px;margin-top: 25px">
                    <span style="font-size: 15px;font-weight: 650;color: #333">关键词</span>
                  </a-col>
                  <a-col :span="24">
                    <a-tag v-for="(item, index) in recordData.keyWord.split(',')" :key="index">{{ item }}</a-tag>
                  </a-col>
                  <br/>
                  <a-col style="margin-bottom: 15px;margin-top: 25px">
                    <span style="font-size: 15px;font-weight: 650;color: #333">全文概要 </span>
                    <span style="font-size: 14px;color: #999"> {{ recordData.paragraphTitle ? recordData.paragraphTitle : '- -' }}</span>
                  </a-col>
                  <a-col :span="24">
                    <div style="font-size: 14px;line-height: 30px">{{ recordData.paragraphSummary ? recordData.paragraphSummary : '- -' }}</div>
                  </a-col>
                  <a-col :span="24" style="margin-top: 15px">
                    <aplayer autoplay :music="{
                              title: recordData.fileName,
                              src: recordData.fileUrl
                            }"/>
                  </a-col>
                  <a-col :span="24" style="margin-top: 25px;">
                    <a-tabs default-active-key="1" :tabBarStyle="{textAlign: 'left'}" :animated="false" @change="callback">
                      <a-tab-pane key="1" tab="原文">
                        <div>
                          <a-list
                            class="comment-list"
                            item-layout="horizontal"
                            :data-source="textPolish">
                            <a-list-item slot="renderItem" slot-scope="item, index">
                              <a-comment>
                                <a-avatar slot="avatar" shape="square" size="large" style="color: #FFFFFF;backgroundColor: #7265e6">
                                  <!-- {{ item.sentenceIds }} -->
                                  {{ checkRole(item.speakerId, item.sentenceIds) }}
                                </a-avatar>
                                <a slot="author" style="font-size: 13px;font-family: SimHei;">
                                  <a-tag color="blue">
                                    {{ formatDateCheck(item.startTime) }} ~ {{ formatDateCheck(item.endTime) }}
                                  </a-tag>
<!--                                  <span style="color: #666">【】</span>-->
                                </a>
                                <div slot="content" style="font-family: SimHei;margin-top: 15px;line-height: 30px">
                                  {{ item.formalParagraphText }}
                                </div>
                              </a-comment>
                            </a-list-item>
                          </a-list>
                        </div>
                      </a-tab-pane>
                      <a-tab-pane key="2" tab="章节速览">
                        <div style="font-family: SimHei;line-height: 50px">
                          <a-list item-layout="horizontal" :data-source="autoChapters">
                            <a-list-item slot="renderItem" slot-scope="item, index">
                              <a-list-item-meta>
                                <div slot="description" style="font-family: SimHei;line-height: 30px">
                                  {{ item.summary }}
                                </div>
                                <a slot="title" style="font-size: 15px;font-family: SimHei;">
                                  <a-badge status="processing" />
                                  <a-tag color="cyan">
                                    {{ formatDateCheck(item.startTime) }} ~ {{ formatDateCheck(item.endTime) }}
                                  </a-tag>
<!--                                  <span style="font-size: 14px">【】</span>-->
                                  <span style="color: #333;font-weight: 650">{{ item.headline }}</span>
                                </a>
                              </a-list-item-meta>
                            </a-list-item>
                          </a-list>
                        </div>
                      </a-tab-pane>
                      <a-tab-pane key="3" tab="发言总结">
                        <div>
                          <a-list
                            class="comment-list"
                            item-layout="horizontal"
                            :data-source="summarizationRecord">
                            <a-list-item slot="renderItem" slot-scope="item, index">
                              <a-comment :author="item.speakerName">
                                <a-avatar slot="avatar" shape="square" size="large" style="color: #FFFFFF;backgroundColor: #7265e6">
                                  {{ item.speakerName }}
                                </a-avatar>
                                <div slot="content" style="font-family: SimHei;margin-top: 15px;line-height: 30px">
                                  {{ item.summary }}
                                </div>
                              </a-comment>
                            </a-list-item>
                          </a-list>
                        </div>
                      </a-tab-pane>
                      <a-tab-pane key="4" tab="问答回顾">
                        <div>
                          <div v-for="(item, index) in questionsAnswerSummary" :key="index">
                            <a-comment>
                              <a slot="author" style="font-size: 14px;font-weight: 650;font-family: SimHei;color: #333">
                                <a-badge :count="index + 1" :number-style="{ backgroundColor: '#1677ff' }"/>
                                {{ item.question }}
                              </a>
                              <a-comment>
                                <div slot="content" style="font-family: SimHei;">
                                  {{ item.answer }}
                                </div>
                              </a-comment>
                            </a-comment>
                            <a-divider />
                          </div>
                        </div>
                      </a-tab-pane>
                      <a-tab-pane key="5" tab="思维导图" v-show="this.mindMapSummary != null" force-render>
                        <a-skeleton :loading="mind.data == null"/>
                        <div style="width: 100%;" v-if="mind.data != null">
                          <js-mind :values="mind" :options="options" mode="side" ref="jsMind" height="1300px"></js-mind>
                        </div>
                      </a-tab-pane>
                    </a-tabs>
                  </a-col>
                </a-row>
              </a-col>
            </a-row>
          </div>
        </a-col>
      </a-row>
    </div>
  </a-drawer>
</template>

<script>
import moment from 'moment'
import Aplayer from 'vue-aplayer'
moment.locale('zh-cn')
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
export default {
  name: 'userView',
  props: {
    recordShow: {
      type: Boolean,
      default: false
    },
    recordData: {
      type: Object
    }
  },
  components: {
    Aplayer
  },
  computed: {
    show: {
      get: function () {
        return this.recordShow
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      aiIssue: '',
      currentTab: 1,
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      autoChapters: [],
      summarizationRecord: [],
      questionsAnswerSummary: [],
      textPolish: [],
      yearRecordRate: [],
      recordList: [],
      callRecord: [],
      callRecordContuineTip: [],
      voiceFeature: null,
      complaintMoods: [],
      mindMapSummary: null,
      llmLoading: false,
      replyTemplateId: '',
      replyTemplateList: [],
      mind: {
        /* 元数据，定义思维导图的名称、作者、版本等信息 */
        meta: {
          name: '思维导图',
          version: '0.2'
        },
        /* 数据格式声明 */
        format: 'node_tree',
        /* 数据内容 */
        data: null
      },
      options: {
        container: 'jsmind_container', // [必选] 容器的ID
        editable: false, // [可选] 是否启用编辑
        theme: 'nephrite', // [可选] 主题
        view: {
          engine: 'canvas', // 思维导图各节点之间线条的绘制引擎
          hmargin: 120, // 思维导图距容器外框的最小水平距离
          vmargin: 50, // 思维导图距容器外框的最小垂直距离
          line_width: 2, // 思维导图线条的粗细
          line_color: '#ddd' // 思维导图线条的颜色
        },
        layout: {
          hspace: 100, // 节点之间的水平间距
          vspace: 20, // 节点之间的垂直间距
          pspace: 20 // 节点与连接线之间的水平间距（用于容纳节点收缩/展开控制器）
        },
        shortcut: {
          enable: false // 是否启用快捷键 默认为true
        }
      },
      series: [{
        name: '投诉数量',
        data: []
      }],
      chartOptions: {
        chart: {
          type: 'bar',
          height: 380
        },
        xaxis: {
          type: 'category',
          group: {
            style: {
              fontSize: '10px',
              fontWeight: 700
            }
          }
        },
        title: {
          text: '用户投诉统计'
        }
      }
    }
  },
  watch: {
    recordShow: function (value) {
      if (value) {
        this.currentTab = 1
        this.selectVoiceRecordDetail(this.recordData.id)
      }
    },
    callRecord: function (value) {
      this.genderCallRecordContinueTip(value)
    }
  },
  methods: {
    checkRole (speakerId, sentenceIds) {
      if (!speakerId) {
        return sentenceIds
      }
      if (this.recordData.complaintSpeakId && this.recordData.complaintSpeakId === speakerId) {
        return '投诉人'
      }
      if (this.recordData.operatorSpeakId && this.recordData.operatorSpeakId === speakerId) {
        return '接线员'
      }
      return sentenceIds
    },
    /**
     * tab切换
     * @param key
     */
    callback (key) {
      if (key === '5') {
        if (this.mind.data == null) {
          setTimeout(() => {
            this.mind.data = this.mindMapSummary
          }, 200)
        }
      }
    },
    /**
     * 生成继续生成提示
     */
    genderCallRecordContinueTip (value) {
      let regex = /话术[一二三四五六七八九]/g
      console.log('tips value ——>', value)
      let tips = []
      tips.push('继续提案')
      tips.push('重新提案')
      if (value.length > 0) {
        let text = value[0].outputText
        let matchs = text.match(regex)
        console.log(matchs)
        matchs.forEach(item => {
          tips.push('优化"' + item + '"')
        })
      }
      this.callRecordContuineTip = tips
    },

    /**
     * 跳转到模板管理功能
     */
    toTemplateManage () {
      console.log('创建模板')
      this.$router.push('/manage/replyTemplate')
    },

    /**
     * 调用LLM生成回答
     */
    callLLM () {
      console.log(this.replyTemplateId)
      if (this.replyTemplateId === '') {
        this.$message.warning('请选择回复模板')
        return 0
      }
      let recordId = this.recordData.id
      this.llmLoading = true
      this.$get(`/cos/LLMCallRecord/callLLM/${recordId}?templateId=${this.replyTemplateId}`).then((r) => {
        this.callRecord = r.data.data
        this.llmLoading = false
      })
    },

    continueCallLLM () {
      if (this.callRecord.length === 0) {
        return
      }
      this.llmLoading = true
      let recordId = this.recordData.id
      let sessionId = this.callRecord[0].sessionId
      this.$get(`/cos/LLMCallRecord/continueCallLLM/${recordId}?sessionId=${sessionId}&content=${this.aiIssue}`).then((r) => {
        this.callRecord = r.data.data
        this.llmLoading = false
      })
    },

    /**
     * 音频时间格式化
     * @param millisecond 毫秒
     * @returns {number|*} 结果
     */
    formatDateCheck (millisecond) {
      if (millisecond > 0) {
        const date = moment.duration(millisecond, 'millisecond')
        const hours = Math.floor(date._data.hours)
        const minutes = Math.floor(date._data.minutes)
        const seconds = Math.floor(date._data.seconds)
        return (hours > 0 ? `${hours < 10 ? '0' : ''}${hours}:` : '00:') +
          (minutes > 0 ? `${minutes < 10 ? '0' : ''}${minutes}:` : '00:') +
          (seconds > 0 ? `${seconds < 10 ? '0' : ''}${seconds}` : '00')
      } else {
        return 0
      }
    },
    /**
     * 获取语音解析详情
     */
    selectVoiceRecordDetail (recordId) {
      this.loading = true
      this.$get(`/cos/voice-analysis-record/${recordId}`).then((r) => {
        let data = r.data
        this.autoChapters = data.autoChapters
        this.summarizationRecord = data.summarizationRecord
        this.questionsAnswerSummary = data.questionsAnswerSummary
        this.textPolish = data.textPolish
        this.recordList = data.recordList
        this.callRecord = data.callRecord
        this.voiceFeature = data.voiceFeature
        this.complaintMoods = data.voiceAnalysis.complaintMood === null ? [] : data.voiceAnalysis.complaintMood.split(',')
        let yearRecordRate = data.yearRecordRate

        let yearRecordRateData = []
        yearRecordRate.forEach(item => {
          yearRecordRateData.push({x: item.name, y: item.value})
        })
        this.series[0].data = yearRecordRateData

        if (data.mindMapSummary) {
          this.mindMapSummary = data.mindMapSummary
          // this.mind.data = this.mindMapSummary
        }
        this.loading = false
      })
      this.handleReplyTemplateSearch('')
    },
    /**
     * 获取回复模板list
     * @param {} keyword
     */
    handleReplyTemplateSearch (keyword) {
      console.log(keyword)
      console.log(this.replyTemplateKeyword)
      this.$get(`/cos/LLMReplyTemplate/list?keyword=${keyword}`).then((r) => {
        console.log('list->', r.data.data)
        let array = []
        r.data.data.forEach(item => {
          array.push({label: item.templateName, value: item.templateId})
        })
        this.replyTemplateList = array
      })
    },

    handleReplyTemplateChange () {
    },

    imagesInit (images) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.fileList = imageList
      }
    },
    handleCancel () {
      this.previewVisible = false
    },
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    picHandleChange ({ fileList }) {
      this.fileList = fileList
    },
    /**
     * 音频下载
     */
    onDownload () {
      window.open(this.recordData.fileUrl, '_blank')
    },
    onClose () {
      this.mind.data = null
      this.$emit('close')
    }
  }
}
</script>

<style scoped>
  >>> .aplayer {
    margin: 0;
  }
  >>> .ant-drawer-body {
    padding: 0 !important;
    background-color: #f5f5f5;
  }
  >>> .ant-card-meta-title {
    font-size: 13px;
    font-family: SimHei;
  }
  >>> .ant-card-meta-description {
    font-size: 12px;
    font-family: SimHei;
  }
  >>> .ant-divider-with-text-left {
    margin: 0;
  }

  >>> .ant-card-head-title {
    font-size: 13px;
    font-family: SimHei;
  }
  >>> .ant-card-extra {
    font-size: 13px;
    font-family: SimHei;
  }
  >>> .ant-radio-button-wrapper {
    border-radius: 0;
  }
  #jsmind_container {
    width: 100%;
    height: 800px;
  }
</style>
