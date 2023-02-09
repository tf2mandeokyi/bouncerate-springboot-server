import React from 'react'
import Title from '../../components/title';
import MainPageScheduleTable from './schedule-table';

import './index.scss'


const Home : React.FC = () => {
    return (
        <div className='page-content'>
            <Title>홈쇼핑 광고 편성표</Title>
            <div className='non-table'>
                <div className='expected-br-span'>예상 Bounce rate: 50%</div>
                <div className='br-control'>
                    <div className='control-item gray'>
                        Bounce rate 구간 조정
                        <div>
                            <input type='number' value={ 0 }></input> % ~
                            <input type='number' value={ 30 }></input> %
                        </div>
                    </div>
                    <div className='control-item right'>
                        <div className='button darkblue'>업데이트</div>
                    </div>
                </div>
            </div>
            <MainPageScheduleTable />
        </div>
    )
}

export default Home;