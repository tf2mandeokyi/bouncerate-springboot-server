import React from 'react'

import './index.css'


type Props = {
    date: Date
}

const LastUpdateDiv : React.FC<Props> = ({ date }) => {
    return (
        <div className='last-update'>
            (마지막 업데이트: { date.toString() })
        </div>
    )
}

export default LastUpdateDiv