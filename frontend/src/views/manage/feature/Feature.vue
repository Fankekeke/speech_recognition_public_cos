<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row :gutter="15">
          <div :class="advanced ? null: 'fold'">
            <a-col :md="6" :sm="24">
              <a-form-item
                label="特征编号"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.code"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="类型"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-select v-model="queryParams.type">
                  <a-select-option value="0">投诉人</a-select-option>
                  <a-select-option value="1">接线员</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </div>
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
            <a-button style="margin-left: 8px" @click="reset">重置</a-button>
          </span>
        </a-row>
      </a-form>
    </div>
    <div>
      <div class="operator">
        <a-button type="primary" ghost @click="add">接线员人声录入</a-button>
        <a-button type="danger" @click="batchDelete">删除</a-button>
      </div>
      <!-- 表格区域 -->
      <a-table ref="TableInfo"
               :columns="columns"
               :rowKey="record => record.id"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :rowClassName="(record, index) => (index % 2 === 1 ? 'table-striped' : null)"
               :scroll="{ x: 900 }"
               @change="handleTableChange">
        <template slot="operation" slot-scope="text, record">
          <a-icon type="setting" theme="twoTone" twoToneColor="#4a9ff5" @click="edit(record)" title="编 辑"></a-icon>
        </template>
      </a-table>
    </div>
    <record-add
      v-if="recordAdd.visiable"
      @close="handleRecordAddClose"
      @success="handleRecordAddSuccess"
      :recordAddVisiable="recordAdd.visiable">
    </record-add>
    <record-edit
      ref="recordEdit"
      @close="handleRecordEditClose"
      @success="handleRecordEditSuccess"
      :recordEditVisiable="recordEdit.visiable">
    </record-edit>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import {mapState} from 'vuex'
import moment from 'moment'
import RecordAdd from './FeatureAdd.vue'
import RecordEdit from './FeatureEdit.vue'
moment.locale('zh-cn')

export default {
  name: 'record',
  components: {RecordAdd, RecordEdit, RangeDate},
  data () {
    return {
      recordAdd: {
        visiable: false
      },
      advanced: false,
      recordView: {
        visiable: false,
        data: null
      },
      recordEdit: {
        visiable: false
      },
      queryParams: {},
      filteredInfo: null,
      sortedInfo: null,
      paginationInfo: null,
      dataSource: [],
      selectedRowKeys: [],
      loading: false,
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      userList: []
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    columns () {
      return [{
        title: '声纹库唯一编号',
        dataIndex: 'code',
        width: 200
      }, {
        title: '类型',
        dataIndex: 'type',
        width: 200,
        customRender: (text, row, index) => {
          switch (text) {
            case '0':
              return <a-tag color="blue">投诉人</a-tag>
            case '1':
              return <a-tag color="pink">接线员</a-tag>
            case '2':
              return <a-tag>其他</a-tag>
            default:
              return '- -'
          }
        }
      }, {
        title: '年龄识别',
        dataIndex: 'ageRate',
        width: 200,
        customRender: (text, row, index) => {
          switch (text) {
            case '0':
              return <a-tag>12~40岁</a-tag>
            case '1':
              return <a-tag>0~12岁</a-tag>
            case '2':
              return <a-tag>40岁以上</a-tag>
            default:
              return '- -'
          }
        }
      }, {
        title: '性别',
        dataIndex: 'sex',
        width: 200,
        customRender: (text, row, index) => {
          switch (text) {
            case '0':
              return <a-tag>女性</a-tag>
            case '1':
              return <a-tag>男性</a-tag>
            case '2':
              return <a-tag>未知</a-tag>
            default:
              return '- -'
          }
        }
      }, {
        title: '备注',
        dataIndex: 'remark',
        width: 250,
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        }
      }, {
        title: '上传时间',
        dataIndex: 'createDate',
        width: 250,
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        }
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: {customRender: 'operation'}
      }]
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
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
    add () {
      this.recordAdd.visiable = true
    },
    edit (record) {
      this.$refs.recordEdit.setFormValues(record)
      this.recordEdit.visiable = true
    },
    handleRecordAddClose () {
      this.recordAdd.visiable = false
      this.search()
    },
    handleRecordAddSuccess () {
      this.recordAdd.visiable = false
      this.$message.success('上传成功')
      this.search()
    },
    handleRecordEditClose () {
      this.recordEdit.visiable = false
    },
    handleRecordEditSuccess () {
      this.recordEdit.visiable = false
      this.$message.success('修改成功')
      this.search()
    },
    recordViewOpen (row) {
      this.recordView.data = row
      this.recordView.visiable = true
    },
    handleRecordViewClose () {
      this.recordView.visiable = false
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    batchDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let ids = that.selectedRowKeys.join(',')
          that.$delete('/cos/voice-feature/' + ids).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.search()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    search () {
      let {sortedInfo, filteredInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fetch({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams,
        ...filteredInfo
      })
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置分页
      this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent
        this.paginationInfo.pageSize = this.pagination.defaultPageSize
      }
      // 重置列过滤器规则
      this.filteredInfo = null
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {}
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data，用于后续使用
      this.paginationInfo = pagination
      this.filteredInfo = filters
      this.sortedInfo = sorter

      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    fetch (params = {}) {
      // 显示loading
      this.loading = true
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
        params.size = this.paginationInfo.pageSize
        params.current = this.paginationInfo.current
      } else {
        // 如果分页信息为空，则设置为默认值
        params.size = this.pagination.defaultPageSize
        params.current = this.pagination.defaultCurrent
      }
      this.$get('/cos/voice-feature/page', {
        ...params
      }).then((r) => {
        let data = r.data.data
        const pagination = {...this.pagination}
        pagination.total = data.total
        this.dataSource = data.records
        this.pagination = pagination
        // 数据加载完毕，关闭loading
        this.loading = false
      })
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>
@import "../../../../static/less/Common";
</style>
