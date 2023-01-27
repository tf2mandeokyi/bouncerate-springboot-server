import React from 'react'
import MainPageTableDiv from './table';
import CategoryRanking from '../product-category-menu/product-ranking';


const Home : React.FC = () => {
    return <>
        <CategoryRanking />
        <MainPageTableDiv />
    </>
}

export default Home;