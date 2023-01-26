import React, { useCallback, useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { getBounceRate, setBounceRate } from '../../api/bouncerate';
import { AdvertisementProduct, getProduct } from '../../api/products';
import { getSetTopBoxesCount, getSetTopBoxesPage, SetTopBox } from '../../api/settopboxes';
import BackToHome from '../../components/back-to-home';
import EntityDescriptionTable from '../../components/entity-description';
import EntityTable, { EntityToJSXFunction } from '../../components/entity-table';


const AdvertisementProductMenu : React.FC = () => {

    const location = useLocation();
    const params = new URLSearchParams(location.search);
    const productId = parseInt(params.get('id') ?? '-1');

    const [ product, setProduct ] = useState<AdvertisementProduct>();
    const [ updateBool, setUpdateBool ] = useState<boolean>();


    const onBounceRateEditButtonClick = useCallback(async (setTopBox: SetTopBox, update: () => void) => {
        let promptInput = prompt('새로운 Bounce rate 값을 입력해주세요.');
        if(!promptInput) return;

        let newBounceRate = parseFloat(promptInput);
        if(isNaN(newBounceRate)) return;

        await setBounceRate({ productId, setTopBoxId: setTopBox.id }, newBounceRate);
        update();
        setUpdateBool(true);
    }, [ productId ]);


    const getEntityCount = useCallback(async () => await getSetTopBoxesCount(), []);
    const getEntitiesPage = useCallback(async (e: number, p: number) => await getSetTopBoxesPage(e, p), []);
    const entityToJSX : EntityToJSXFunction<SetTopBox> = useCallback(async (setTopBox, update) => {
        let bounceRate = await getBounceRate({ productId, setTopBoxId: setTopBox.id });
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
    }, [ productId, onBounceRateEditButtonClick ]);


    useEffect(() => {
        (async () => {
            setProduct(await getProduct(productId));
        })();
        if(updateBool) {
            setUpdateBool(false);
        }
    }, [ productId, setProduct, updateBool ]);


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
                entityNameColumnHead={ [ '셋톱박스 이름', 'Bounce rate' ] }
                getEntityCount={ getEntityCount }
                getEntitiesPage={ getEntitiesPage }
                entityToJSX={ entityToJSX }
            />
        </>
    ) : <>Loading...</>
    
}

export default AdvertisementProductMenu;