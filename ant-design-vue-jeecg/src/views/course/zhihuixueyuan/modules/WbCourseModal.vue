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
            <a-form-item label="所属类别" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-category-select v-decorator="['category']" pcode="A01A01" placeholder="请选择所属类别" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="展示图片" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="[ 'image', validatorRules.image]" placeholder="请输入展示图片"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="课程简介" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="[ 'introduction', validatorRules.introduction]" placeholder="请输入课程简介"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="视频文件" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="[ 'videoUrl', validatorRules.videoUrl]" placeholder="请输入视频文件"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="音频文件" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="[ 'audioUrl', validatorRules.audioUrl]" placeholder="请输入音频文件"></a-input>
            </a-form-item>
          </a-col>

          <a-col :span="24">
            <a-form-item label="课程描述" :labelCol="labelCol2" :wrapperCol="wrapperCol2">
              <a-input v-decorator="[ 'description', validatorRules.description]" placeholder="请输入课程描述"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="讲师姓名" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="[ 'teacherName', validatorRules.teacherName]" placeholder="请输入讲师姓名"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="发布日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-date placeholder="请选择发布日期" v-decorator="[ 'publishDate', validatorRules.publishDate]" :trigger-change="true" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="时长" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-decorator="[ 'duration', validatorRules.duration]" placeholder="请输入时长"></a-input>
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="学习人数" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number v-decorator="[ 'learnNum', validatorRules.learnNum]" placeholder="请输入学习人数" style="width: 100%"/>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="是否置顶" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="radio" v-decorator="['isTop']" :trigger-change="true" dictCode="checkbox_type" placeholder="请选择是否置顶"/>
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="是否免费" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag type="radio" v-decorator="['isFree']" :trigger-change="true" dictCode="checkbox_type" placeholder="请选择是否免费"/>
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
        <a-tab-pane tab="课程评论" :key="refKeys[0]" :forceRender="true">
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
  import JCategorySelect from '@/components/jeecg/JCategorySelect'

  export default {
    name: 'WbCourseModal',
    mixins: [JEditableTableMixin],
    components: {
      JDate,
      JUpload,
      JDictSelectTag,
      JCategorySelect
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
          category: { rules: [{ required: true, message: '请输入所属类别!' }] },
          image:{},
          videoUrl:{},
          audioUrl:{},
          introduction:{},
          description:{},
          publishDate:{},
          teacherName:{},
          price:{},
          duration:{},
          classNum:{},
          isFree:{},
          isTop:{},
          learnNum:{},
          sortNo:{},
        },
        refKeys: ['wbCourseComment' ],
        tableKeys:['wbCourseComment' ],
        activeKey: 'wbCourseComment',
        // 万邦课程评论
        wbCourseCommentTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '评论内容',
              key: 'content',
              type: FormTypes.input,
              width:"1000px",
              placeholder: '请输入${title}',
              defaultValue: '',
            },
            {
              title: '发布',
              key: 'publish',
              type: FormTypes.select,
              dictCode:"checkbox_type",
              width:"150px",
              placeholder: '请输入${title}',
              defaultValue: '',
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
        let fieldval = pick(this.model,'title','category','image','videoUrl','audioUrl','introduction','description','publishDate','teacherName','duration','isFree','isTop','learnNum','sortNo')
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
       this.form.setFieldsValue(pick(row,'title','category','image','videoUrl','audioUrl','introduction','description','publishDate','teacherName','duration','isFree','isTop','learnNum','sortNo'))
     },
     handleCategoryChange(value,backObj){
       this.form.setFieldsValue(backObj)
      }

    }
  }
</script>

<style scoped>
</style>