<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :maskClosable="false"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel">
    <a-spin :spinning="confirmLoading">
      <!-- 主表单区域 -->
      <a-form :form="form">
        <a-row>

          <a-col :span="12">
            <a-form-item label="课程标题" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="[ 'title', validatorRules.title]" placeholder="请输入课程标题"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="所属分类" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['type']" :trigger-change="true" dictCode="course_type" placeholder="请选择所属分类"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="所属类别" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-multi-select-tag type="list_multi" v-decorator="['category']" :trigger-change="true" dictCode="course_category" placeholder="请选择所属类别"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="展示图片" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-upload v-decorator="['image']" :trigger-change="true"></j-upload>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="视频文件" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-upload v-decorator="['videoUrl']" :trigger-change="true"></j-upload>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="音频文件" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-upload v-decorator="['audioUrl']" :trigger-change="true"></j-upload>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="课程简介" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-upload v-decorator="['introduction']" :trigger-change="true"></j-upload>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="课程描述" :labelCol="labelCol2" :wrapperCol="wrapperCol2">
              <a-textarea v-decorator="['description']" rows="4" placeholder="请输入课程描述"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="发布日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择发布日期" v-decorator="[ 'publishDate', validatorRules.publishDate]" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="讲师姓名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="[ 'teacherName', validatorRules.teacherName]" placeholder="请输入讲师姓名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="专题" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="list" v-decorator="['specialTopic']" :trigger-change="true" dictCode="special_topics" placeholder="请选择专题"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="学习人数" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="[ 'learnNum', validatorRules.learnNum]" placeholder="请输入学习人数" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="排序" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="[ 'sortNo', validatorRules.sortNo]" placeholder="请输入排序" style="width: 100%"/>
            </a-form-item>
          </a-col>

        </a-row>
      </a-form>

      <!-- 子表单区域 -->
      <a-tabs v-model="activeKey" @change="handleChangeTabs">
        <a-tab-pane tab="万邦课程评论" :key="refKeys[0]" :forceRender="true">
          <j-editable-table
            :ref="refKeys[0]"
            :loading="wbCourseCommentTable.loading"
            :columns="wbCourseCommentTable.columns"
            :dataSource="wbCourseCommentTable.dataSource"
            :maxHeight="300"
            :rowNumber="true"
            :rowSelection="true"
            :actionButton="true"/>
        </a-tab-pane>
        
      </a-tabs>

    </a-spin>
  </a-modal>
</template>

<script>

  import pick from 'lodash.pick'
  import { FormTypes,getRefPromise } from '@/utils/JEditableTableUtil'
  import { JEditableTableMixin } from '@/mixins/JEditableTableMixin'
  import JDate from '@/components/jeecg/JDate'  
  import JUpload from '@/components/jeecg/JUpload'
  import JDictSelectTag from "@/components/dict/JDictSelectTag"
  import JMultiSelectTag from "@/components/dict/JMultiSelectTag"

  export default {
    name: 'WbCourseModal',
    mixins: [JEditableTableMixin],
    components: {
      JDate,
      JUpload,
      JDictSelectTag,
      JMultiSelectTag,
    },
    data() {
      return {
        labelCol: {
          span: 6
        },
        wrapperCol: {
          span: 16
        },
        labelCol2: {
          span: 3
        },
        wrapperCol2: {
          span: 20
        },
        // 新增时子表默认添加几行空数据
        addDefaultRowNum: 1,
        validatorRules: {
          title: { rules: [{ required: true, message: '请输入课程标题!' }] },
          type: { rules: [{ required: true, message: '请输入所属分类!' }] },
          category: { rules: [{ required: true, message: '请输入所属类别!' }] },
          image:{},
          videoUrl:{},
          audioUrl:{},
          introduction:{},
          description:{},
          publishDate:{},
          teacherName:{},
          specialTopic:{},
          learnNum:{},
          sortNo:{},
        },
        refKeys: ['wbCourseComment', ],
        tableKeys:['wbCourseComment', ],
        activeKey: 'wbCourseComment',
        // 万邦课程评论
        wbCourseCommentTable: {
          loading: true,
          dataSource: [],
          columns: [
            {
              title: '评论内容',
              key: 'content',
              type: FormTypes.input,
              width:"800px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '发布',
              key: 'publish',
              type: FormTypes.select,
              dictCode:"checkbox_type",
              width:"200px",
              placeholder: '请选择是否${title}',
              defaultValue: '0',
            },
          ]
        },
        url: {
          add: "/course/wbCourse/add",
          edit: "/course/wbCourse/edit",
          wbCourseComment: {
            list: '/course/wbCourse/queryWbCourseCommentByMainId'
          },
        }
      }
    },
    methods: {
      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },
      /** 调用完edit()方法之后会自动调用此方法 */
      editAfter() {
        let fieldval = pick(this.model,'title','type','category','image','videoUrl','audioUrl','introduction','description','publishDate','teacherName','specialTopic','learnNum','sortNo')
        this.$nextTick(() => {
          this.form.setFieldsValue(fieldval)
        })
        // 加载子表数据
        if (this.model.id) {
          let params = { id: this.model.id }
          this.requestSubTableData(this.url.wbCourseComment.list, params, this.wbCourseCommentTable)
        }
      },
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)

        return {
          ...main, // 展开
          wbCourseCommentList: allValues.tablesValue[0].values,
        }
      },
      validateError(msg){
        this.$message.error(msg)
      },
     popupCallback(row){
       this.form.setFieldsValue(pick(row,'title','type','category','image','videoUrl','audioUrl','introduction','description','publishDate','teacherName','specialTopic','learnNum','sortNo'))
     },

    }
  }
</script>

<style scoped>
</style>