import React, { useEffect, useState } from "react";
import MyTraceItem from './../components/spacepage/MyTraceItem';
import { getMyTrace } from "../api/spaceApi";
import Header from './../components/common/Header';
import CreateButton from './../components/common/CreateButton';

const MyTracePage = () => {
  const [traceList, setTraceList] = useState([])


  // 내 방명록 조회 axios 호출
  useEffect(() => {
    getMyTrace()
      .then((res) => {
        setTraceList(res.data)
        // console.log('내 방명록 잘 옴??', res.data)
      })
      .catch((err) => {
        console.log('내 방명록 가져오기  실패ㄱ-', err)
      })
  }, [])

  return (
    <>
      <div className="bg-white h-full">
        <Header title="내 방명록" to='/space' />
        <div className='px-5 pt-5 overflow-y-auto max-h-[calc(100vh-160px)]'>

          <ul>
            {traceList.map((traceData, index) => (
              <MyTraceItem key={index} data={traceData} />
            ))}
          </ul>
          <CreateButton to='/space/upload' />
        </div>
      </div>
    </>
  );
};

export default MyTracePage;