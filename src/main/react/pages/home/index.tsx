import React from 'react'
import MainPageTableDiv from './table';
import ProductRanking from '../advertisement-product-menu/product-ranking';


const Home : React.FC = () => {
    return <>
        <ProductRanking />
        <MainPageTableDiv />
    </>
}

export default Home;