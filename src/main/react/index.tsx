import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter, Route, Routes } from 'react-router-dom'

import Home from './pages/home'
import ProductCategoryMenu from './pages/product-category-menu'
import SetTopBoxMenu from './pages/set-top-box-menu'

import './index.scss';


const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);

root.render(
    <BrowserRouter>
        <div className='main'>
            <Routes>
                <Route index element={ <Home /> } />
                <Route path='/products' element={ <ProductCategoryMenu /> } />
                <Route path='/settopboxes' element={ <SetTopBoxMenu /> } />
            </Routes>
        </div>
    </BrowserRouter>
);