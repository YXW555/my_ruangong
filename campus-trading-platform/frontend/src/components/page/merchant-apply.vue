<template>
    <div>
        <app-head></app-head>
        <app-body>
            <div class="merchant-apply-container">
                <el-card class="apply-card" shadow="hover">
                    <div slot="header" class="card-header">
                        <span><i class="el-icon-s-shop"></i> 商家认证申请</span>
                    </div>

                    <!-- 申请状态提示 -->
                    <div v-if="applicationStatus" class="status-info">
                        <el-alert
                            :title="getStatusTitle()"
                            :type="getStatusType()"
                            :description="getStatusDescription()"
                            show-icon
                            :closable="false">
                        </el-alert>
                    </div>

                    <!-- 申请表单 -->
                    <el-form
                        :model="applicationForm"
                        :rules="rules"
                        ref="applicationForm"
                        label-width="120px"
                        class="apply-form"
                        v-if="!applicationStatus || applicationStatus.status === 2">
                        <el-form-item label="店铺名称" prop="shopName">
                            <el-input
                                v-model="applicationForm.shopName"
                                placeholder="请输入店铺名称"
                                maxlength="32"
                                show-word-limit>
                            </el-input>
                        </el-form-item>

                        <el-form-item label="联系电话" prop="contactPhone">
                            <el-input
                                v-model="applicationForm.contactPhone"
                                placeholder="请输入联系电话"
                                maxlength="16">
                            </el-input>
                        </el-form-item>

                        <el-form-item label="联系地址" prop="contactAddress">
                            <el-input
                                v-model="applicationForm.contactAddress"
                                type="textarea"
                                :rows="2"
                                placeholder="请输入详细联系地址"
                                maxlength="128"
                                show-word-limit>
                            </el-input>
                        </el-form-item>

                        <el-form-item label="身份证正面" prop="idCardFront">
                            <el-upload
                                action="/file/"
                                :on-success="(res) => handleImageSuccess(res, 'idCardFront')"
                                :show-file-list="false"
                                accept="image/*"
                                class="image-uploader">
                                <el-image
                                    v-if="applicationForm.idCardFront"
                                    :src="applicationForm.idCardFront"
                                    fit="cover"
                                    class="uploaded-image">
                                </el-image>
                                <i v-else class="el-icon-plus image-uploader-icon"></i>
                            </el-upload>
                            <div class="upload-tip">请上传身份证正面照片</div>
                        </el-form-item>

                        <el-form-item label="身份证反面" prop="idCardBack">
                            <el-upload
                                action="/file/"
                                :on-success="(res) => handleImageSuccess(res, 'idCardBack')"
                                :show-file-list="false"
                                accept="image/*"
                                class="image-uploader">
                                <el-image
                                    v-if="applicationForm.idCardBack"
                                    :src="applicationForm.idCardBack"
                                    fit="cover"
                                    class="uploaded-image">
                                </el-image>
                                <i v-else class="el-icon-plus image-uploader-icon"></i>
                            </el-upload>
                            <div class="upload-tip">请上传身份证反面照片</div>
                        </el-form-item>

                        <el-form-item label="营业执照" prop="businessLicense">
                            <el-upload
                                action="/file/"
                                :on-success="(res) => handleImageSuccess(res, 'businessLicense')"
                                :show-file-list="false"
                                accept="image/*"
                                class="image-uploader">
                                <el-image
                                    v-if="applicationForm.businessLicense"
                                    :src="applicationForm.businessLicense"
                                    fit="cover"
                                    class="uploaded-image">
                                </el-image>
                                <i v-else class="el-icon-plus image-uploader-icon"></i>
                            </el-upload>
                            <div class="upload-tip">请上传营业执照（可选）</div>
                        </el-form-item>

                        <el-form-item label="申请理由" prop="applicationReason">
                            <el-input
                                v-model="applicationForm.applicationReason"
                                type="textarea"
                                :rows="4"
                                placeholder="请简要说明申请成为经营性卖家的理由..."
                                maxlength="512"
                                show-word-limit>
                            </el-input>
                        </el-form-item>

                        <el-form-item>
                            <el-button type="primary" icon="el-icon-check" @click="submitApplication" :loading="submitting">
                                提交申请
                            </el-button>
                            <el-button @click="resetForm">重置</el-button>
                        </el-form-item>
                    </el-form>
                </el-card>
            </div>
            <app-foot></app-foot>
        </app-body>
    </div>
</template>

<script>
    import AppHead from '../common/AppHeader.vue';
    import AppBody from '../common/AppPageBody.vue';
    import AppFoot from '../common/AppFoot.vue';

    export default {
        name: "merchant-apply",
        components: {
            AppHead,
            AppBody,
            AppFoot
        },
        data() {
            return {
                applicationForm: {
                    shopName: '',
                    contactPhone: '',
                    contactAddress: '',
                    idCardFront: '',
                    idCardBack: '',
                    businessLicense: '',
                    applicationReason: ''
                },
                applicationStatus: null,
                submitting: false,
                rules: {
                    shopName: [
                        { required: true, message: '请输入店铺名称', trigger: 'blur' },
                        { min: 2, max: 32, message: '长度在 2 到 32 个字符', trigger: 'blur' }
                    ],
                    contactPhone: [
                        { required: true, message: '请输入联系电话', trigger: 'blur' },
                        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
                    ],
                    contactAddress: [
                        { required: true, message: '请输入联系地址', trigger: 'blur' },
                        { min: 5, max: 128, message: '长度在 5 到 128 个字符', trigger: 'blur' }
                    ],
                    idCardFront: [
                        { required: true, message: '请上传身份证正面', trigger: 'change' }
                    ],
                    idCardBack: [
                        { required: true, message: '请上传身份证反面', trigger: 'change' }
                    ],
                    applicationReason: [
                        { max: 512, message: '最多512个字符', trigger: 'blur' }
                    ]
                }
            };
        },
        created() {
            this.loadApplicationStatus();
        },
        methods: {
            loadApplicationStatus() {
                this.$api.getMyMerchantApplication().then(res => {
                    if (res.status_code === 1 && res.data) {
                        this.applicationStatus = res.data;
                        if (res.data.status === 0) {
                            // 待审核状态，填充表单数据
                            this.applicationForm = {
                                shopName: res.data.shopName || '',
                                contactPhone: res.data.contactPhone || '',
                                contactAddress: res.data.contactAddress || '',
                                idCardFront: res.data.idCardFront || '',
                                idCardBack: res.data.idCardBack || '',
                                businessLicense: res.data.businessLicense || '',
                                applicationReason: res.data.applicationReason || ''
                            };
                        }
                    }
                }).catch(() => {
                });
            },
            handleImageSuccess(res, field) {
                if (res.status_code === 1) {
                    this.applicationForm[field] = res.data;
                    this.$refs.applicationForm.validateField(field);
                } else {
                    this.$message.error('图片上传失败');
                }
            },
            submitApplication() {
                this.$refs.applicationForm.validate((valid) => {
                    if (valid) {
                        this.submitting = true;
                        this.$api.submitMerchantApplication(this.applicationForm).then(res => {
                            if (res.status_code === 1) {
                                this.$message({
                                    message: '申请提交成功，请等待审核！',
                                    type: 'success'
                                });
                                this.loadApplicationStatus();
                            } else {
                                this.$message.error('申请提交失败：' + (res.msg || '未知错误'));
                            }
                        }).catch(() => {
                            this.$message.error('申请提交失败！');
                        }).finally(() => {
                            this.submitting = false;
                        });
                    }
                });
            },
            resetForm() {
                this.$refs.applicationForm.resetFields();
            },
            getStatusTitle() {
                if (!this.applicationStatus) return '';
                const status = this.applicationStatus.status;
                if (status === 0) return '审核中';
                if (status === 1) return '审核通过';
                if (status === 2) return '审核未通过';
                return '';
            },
            getStatusType() {
                if (!this.applicationStatus) return 'info';
                const status = this.applicationStatus.status;
                if (status === 0) return 'warning';
                if (status === 1) return 'success';
                if (status === 2) return 'error';
                return 'info';
            },
            getStatusDescription() {
                if (!this.applicationStatus) return '';
                const status = this.applicationStatus.status;
                if (status === 0) {
                    return '您的申请正在审核中，请耐心等待...';
                }
                if (status === 1) {
                    return '恭喜！您已成为经营性卖家，可以享受批量发布、库存管理等高级功能。';
                }
                if (status === 2) {
                    return this.applicationStatus.adminComment || '很抱歉，您的申请未通过审核，可以重新提交申请。';
                }
                return '';
            }
        }
    }
</script>

<style scoped>
    .merchant-apply-container {
        min-height: 85vh;
        padding: 20px;
    }

    .apply-card {
        max-width: 900px;
        margin: 0 auto;
    }

    .card-header {
        font-size: 18px;
        font-weight: 600;
    }

    .status-info {
        margin-bottom: 20px;
    }

    .apply-form {
        padding: 20px 0;
    }

    .image-uploader {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        width: 178px;
        height: 178px;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .image-uploader:hover {
        border-color: #409EFF;
    }

    .image-uploader-icon {
        font-size: 28px;
        color: #8c939d;
    }

    .uploaded-image {
        width: 178px;
        height: 178px;
    }

    .upload-tip {
        font-size: 12px;
        color: #909399;
        margin-top: 5px;
    }
</style>

