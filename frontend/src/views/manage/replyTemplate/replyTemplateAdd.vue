<template>
    <a-modal v-model="show" title="回复模板创建" @cancel="onClose" :maskClosable="false" :width="700" :footer="null">
    <a-form
      :model="form"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      autocomplete="off"
    >
      <a-form-item
        label="模板名称"
        name="templateName"
        :rules="[{ required: true, message: '请输入模板名称' }]"
      >
        <a-input v-model="form.templateName" />
      </a-form-item>
      <a-form-item
        label="模板内容"
        name="templateContent"
        :rules="[{ required: true, message: '请输入回复模板内容' }]"
      >
      <a-textarea v-model="form.templateContent" />
      </a-form-item>

      <a-form-item :wrapper-col="{ offset: 18, span: 20 }">
        <a-button type="primary" html-type="submit" @click="handleSubmit">提交</a-button>
        <a-divider type="vertical" style="height: 60px; background-color: rgba(255, 255, 255, 0)" />
        <a-button @click="onClose" >取消</a-button>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
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
      // form: this.$form.createForm(this),
      loading: false,
      form: {
        templateName: '',
        templateContent: ''
      }
    }
  },
  mounted () {
  },
  methods: {
    handleChange (info) {
      const status = info.file.status
      if (status !== 'uploading') {
        console.log(info.file, info.fileList)
      }
      if (status === 'done') {
        this.$message.success(`${info.file.name} 文件上传成功,正在解析中...`)
        this.onClose()
      } else if (status === 'error') {
        this.$message.error(`${info.file.name} 文件上传失败.`)
      }
    },
    onClose () {
      this.$emit('close')
    },
    handleSubmit () {
      if (this.form.templateName === '') {
        this.$message.warning('模板名称不能为空')
        return
      }
      if (this.form.templateContent === '') {
        this.$message.warning('模板内容不能为空')
        return
      }
      this.$post('/cos/LLMReplyTemplate', this.form).then((r) => {
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
