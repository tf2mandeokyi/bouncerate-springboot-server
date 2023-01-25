import React from 'react'
import MainPageTableDiv from './table';
import ProductRanking from '../../components/product-ranking';

import './index.css'


const Home : React.FC = () => {
    return <>
        <ProductRanking />
        <MainPageTableDiv />
    </>
}

export default Home;