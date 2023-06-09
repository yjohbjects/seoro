import React, { useEffect, useState } from 'react';
import { Button, Modal, Rate, Form, Upload, Input } from 'antd';
import { useParams } from 'react-router-dom';
import { CloseOutlined, FileImageOutlined } from '@ant-design/icons';
import './AddPlaceReviewModal.styles.scss';
import { addPlaceReviewAPI } from '@src/API/placeAPI';
import { useNavigate } from 'react-router-dom';
import { useUser } from '@src/hooks/useUser';
import axios from 'axios';
import type { UploadProps } from 'antd';
import { useMutation, useQueryClient } from 'react-query';
import { placeAPIUrls } from '@src/API/apiUrls';

function Label({ text }: { text: string }) {
  return (
    <h3 style={{ fontSize: '1.2rem', fontFamily: 'NEXON', display: 'flex' }}>
      {text}
    </h3>
  );
}

function AddPlaceReviewModal() {
  const user = useUser();
  const navigate = useNavigate();
  const param = useParams();
  const placeId = param?.id;
  const placeName = param?.placeName;
  const [file, setFile] = useState<any>();
  const [loading, setLoading] = useState(false);
  const [star, setStar] = useState(5);

  const [open, setOpen] = useState(false);
  const [value, setValue] = useState(0);

  const queryCache = useQueryClient();
  async function submitReview(createValues: any) {
    const response = await axios.post(
      `${placeAPIUrls.placeDetail}/${placeId}`,
      createValues,
    );
    return response.data;
  }
  const { mutate } = useMutation(submitReview, {
    onMutate: async updateData => {
      // Save the original todo in case we need to roll back the update
      await queryCache.cancelQueries(`${placeAPIUrls.placeDetail}/${placeId}`);
      const previousTodos = queryCache.getQueryData(
        `${placeAPIUrls.placeDetail}/${placeId}`,
      );
      queryCache.setQueryData(
        `${placeAPIUrls.placeDetail}/${placeId}`,
        (old: any) => {
          console.log(old, updateData);
          return {
            ...old,
            data: {
              ...old.data,
              placeReview: [
                { ...updateData, reviewContent: updateData.placeReview },
                ...old.data.placeReview,
              ],
            },
          };
        },
      );

      return { previousTodos };
    },
  });

  const onFinish = async (values: any) => {
    if (loading) return;
    setLoading(true);
    console.log(star);
    values.placeReview;

    let placeReviewPhotos;
    if (file) {
      const form = new FormData();
      form.append('file', file);
      form.append('upload_preset', 'quzqjwbp');
      const { data } = await axios.post(
        'https://api.cloudinary.com/v1_1/dohkkln9r/image/upload',
        form,
      );
      placeReviewPhotos = [data.url];
    }
    mutate({
      score: star,
      memberName: user?.memberName,
      placeReview: values.placeReview,
      placeReviewPhotos,
    });
    // addPlaceReviewAPI(
    //   {
    //     score: star,
    //     memberName: user?.memberName,
    //     placeReview: values.placeReview,
    //     placeReviewPhotos,
    //   },
    //   placeId,
    // );
    setLoading(false);
  };

  const props: UploadProps = {
    multiple: false,
    customRequest: ({ onSuccess }: any) => onSuccess('ok'),
    itemRender: (_: any, file: any, fileList: any, { remove }: any) => {
      if (fileList.length > 1) {
        if (file != fileList[1]) remove();
        return '';
      }
      const url = URL.createObjectURL(file.originFileObj);
      setFile(file.originFileObj);
      return <img src={url} width="100%" />;
    },
  };

  const [form] = Form.useForm();
  const title = param?.placeName;
  const handleModalClose = () => {
    setOpen(false);
    setValue(0);
    form.resetFields();
  };

  return (
    <div>
      <div onClick={() => setOpen(true)}>
        <Rate defaultValue={0} onChange={e => setStar(e)} />
      </div>

      <Modal
        title="리뷰 작성"
        centered
        open={open}
        onOk={() => setOpen(false)}
        onCancel={() => setOpen(false)}
        width={425}
        okText={<></>}
        cancelText="Custom Cancel Text"
        footer={null}
        afterClose={handleModalClose}
      >
        {/* <div>{placeName}</div> */}

        <div className="line"></div>
        <div className="ant-modal-items">
          <Rate defaultValue={0} onChange={setValue} value={value} />
          <div> {value} / 5</div>
        </div>
        <Form
          form={form}
          onFinish={onFinish}
          name="dynamic_rule"
          style={{ maxWidth: 600 }}
        >
          <div className="line"></div>
          <Form.Item
            label={<Label text="사진 등록" />}
            name="placeReviewPhotos"
            // label="Upload"
            valuePropName="any"
            className="ant-modal-items"
          >
            <Upload.Dragger {...props}>
              <div className="ant-upload-container">
                <p>사진을 추가해주세요</p>
                <FileImageOutlined className="image-icon" />
              </div>
            </Upload.Dragger>
          </Form.Item>
          <Form.Item
            label={<Label text="리뷰 작성" />}
            name="placeReview"
            rules={[{ required: true, message: '리뷰를 작성해주세요' }]}
          >
            <Input.TextArea
              showCount
              maxLength={255}
              style={{ height: 200 }}
              placeholder="리뷰를 작성해주세요"
            />
          </Form.Item>
          <Form.Item className="form-btn" wrapperCol={{ offset: 10, span: 14 }}>
            <Button onClick={() => setOpen(false)}>작성 취소</Button>
            <Button
              type="primary"
              htmlType="submit"
              onClick={() => setOpen(false)}
            >
              작성 완료
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

export default AddPlaceReviewModal;
