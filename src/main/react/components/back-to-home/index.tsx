import React from 'react'

import './index.scss'


const BackToHome : React.FC = (props) => {
    return (
        <div 
            className='back-to-home'
            onClick={ () => { window.location.href = '/' } }
        >&lt;뒤로 가기</div>
    )
}

export default BackToHome