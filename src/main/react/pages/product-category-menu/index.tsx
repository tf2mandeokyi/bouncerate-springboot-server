import React, { useCallback, useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { getBounceRate, setBounceRate } from '../../api/bouncerate';
import { ProductCategory, getProduct } from '../../api/products';
import { getSetTopBoxesCount, getSetTopBoxesPage, SetTopBox } from '../../api/settopboxes';
import BackToHome from '../../components/back-to-home';
import EntityDescriptionTable from '../../components/entity-description';
import EntityTable, { EntityToJSXFunction } from '../../components/entity-table';


const ProductCategoryMenu : React.FC = () => {

    const location = useLocation();
    const params = new URLSearchParams(location.search);
    const categoryId = parseInt(params.get('id') ?? '-1');

    const [ product, setProduct ] = useState<ProductCategory>();
    const [ updateBool, setUpdateBool ] = useState<boolean>();


    const onBounceRateEditButtonClick = useCallback(async (setTopBox: SetTopBox, update: () => void) => {
        let newBounceRate = parseFloat(prompt('새로운 Bounce rate 값을 입력해주세요.') as string);
        if(isNaN(newBounceRate)) return;

        await setBounceRate({ categoryId, setTopBoxId: setTopBox.id }, newBounceRate);
        update();
        setUpdateBool(true);
    }, [ categoryId ]);


    const entityToJSX : EntityToJSXFunction<SetTopBox> = useCallback(async (setTopBox, update) => {
        let bounceRate = await getBounceRate({ categoryId, setTopBoxId: setTopBox.id });
        return [ 
            <>{ bounceRate ?? '-' }</>, 
            <div 
                key={ setTopBox.id } 
                className='button darkblue'
                onClick={ () => onBounceRateEditButtonClick(setTopBox, update) }
            >
                수정
            </div>
        ]
    }, [ categoryId, onBounceRateEditButtonClick ]);


    useEffect(() => {
        (async () => {
            setProduct(await getProduct(categoryId));
        })();
        if(updateBool) {
            setUpdateBool(false);
        }
    }, [ categoryId, setProduct, updateBool ]);


    return product ? (
        <>
            <BackToHome />
            <EntityDescriptionTable>
                <tr><td>이름:</td><td>{ product.name }</td></tr>
                <tr><td>데이터베이스 ID:</td><td>{ product.id }</td></tr>
                <tr><td>광고 가능 여부:</td><td>{ product.availability ? '가능' : '불가능' }</td></tr>
                <tr><td>Bounce rate 점수:</td><td>{ product.bounceRateScore }</td></tr>
            </EntityDescriptionTable>
            <EntityTable<SetTopBox>
                tableHeadColumn={ [ <>셋톱박스 이름</>, <>Bounce rate</> ] }
                getEntityCount={ getSetTopBoxesCount }
                getEntitiesPage={ getSetTopBoxesPage }
                entityToJSX={ entityToJSX }
            />
        </>
    ) : <>Loading...</>
    
}

export default ProductCategoryMenu;