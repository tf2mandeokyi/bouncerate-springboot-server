import React from 'react'
import MainPageTableDiv from '../../components/main-page-table';
import ProductRanking from '../../components/product-ranking';

const Home : React.FC = (props) => {
    return <>
        <ProductRanking />
        <MainPageTableDiv />
    </>
}

export default Home;