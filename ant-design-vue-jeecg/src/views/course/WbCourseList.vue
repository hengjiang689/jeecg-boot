<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="8">
            <a-form-item label="课程标题">
              <a-input placeholder="请输入课程标题" v-model="queryParam.title"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="所属分类">
              <j-dict-select-tag placeholder="请选择所属分类" v-model="queryParam.type" dictCode="course_type"/>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
              <a-form-item label="所属类别">
                <j-dict-select-tag placeholder="请输入所属类别" v-model="queryParam.category" dictCode="course_category"></j-dict-select-tag>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="发布日期">
                <j-date placeholder="请选择发布日期" v-model="queryParam.publishDate"></j-date>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="讲师姓名">
                <a-input placeholder="请输入讲师姓名" v-model="queryParam.teacherName"></a-input>
              </a-form-item>
            </a-col>
          </template>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->
    
    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('万邦课程表')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无此图片</span>
          <img v-else :src="getImgView(text)" height="25px" alt="图片不存在" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无此文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="uploadFile(text)">
            下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <wbCourse-modal ref="modalForm" @ok="modalFormOk"></wbCourse-modal>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import WbCourseModal from './modules/WbCourseModal'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import JDate from '@/components/jeecg/JDate.vue'
  import {initDictOptions, filterMultiDictText} from '@/components/dict/JDictSelectUtil'

  export default {
    name: "WbCourseList",
    mixins:[JeecgListMixin],
    components: {
      JDictSelectTag,
      JDate,
      WbCourseModal
    },
    data () {
      return {
        description: '万邦课程表管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'课程id',
            align:"center",
            width:60,
            dataIndex: 'id'
          },
          {
            title:'课程标题',
            align:"center",
            dataIndex: 'title'
          },
          {
            title:'所属分类',
            align:"center",
            dataIndex: 'type',
            customRender:(text)=>{
              if(!text){
                return ''
              }else{
                return filterMultiDictText(this.dictOptions['type'], text+"")
              }
            }
          },
          {
            title:'所属类别',
            align:"center",
            dataIndex: 'category',
            customRender:(text)=>{
              if(!text){
                return ''
              }else{
                return filterMultiDictText(this.dictOptions['category'], text+"")
              }
            }
          },
          {
            title:'发布日期',
            align:"center",
            dataIndex: 'publishDate',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'讲师姓名',
            align:"center",
            dataIndex: 'teacherName'
          },
          {
            title:'学习人数',
            align:"center",
            dataIndex: 'learnNum'
          },
          {
            title:'排序',
            align:"center",
            dataIndex: 'sortNo'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/course/wbCourse/list",
          delete: "/course/wbCourse/delete",
          deleteBatch: "/course/wbCourse/deleteBatch",
          exportXlsUrl: "/course/wbCourse/exportXls",
          importExcelUrl: "course/wbCourse/importExcel",
        },
        dictOptions:{
         type:[],
         category:[],
        },

      }
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      initDictConfig(){
        initDictOptions('course_type').then((res) => {
          if (res.success) {
            this.$set(this.dictOptions, 'type', res.result)
          }
        })
        initDictOptions('course_category').then((res) => {
          if (res.success) {
            this.$set(this.dictOptions, 'category', res.result)
          }
        })
      }
       
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>